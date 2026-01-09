document.addEventListener('DOMContentLoaded', async function () {

    const form = document.getElementById('leaveForm');
    const leaveRequestsList = document.getElementById('leaveRequestsList');
    const createLeaveButton = document.getElementById('createLeaveButton');

    const deleteModalOverlay = document.getElementById('deleteModalOverlay');
    const deleteCancelButton = document.getElementById('deleteCancelButton');
    const deleteConfirmButton = document.getElementById('deleteConfirmButton');

    const filtersContainer = document.getElementById('leaveFilters');

    let isSubmitting = false;
    let currentUser = null;
    let deleteTargetId = null;

    // filters (used only on leaves list page)
    let filterMyRequests = false;
    let filterMyDepartment = false;
    let allLeaveRequests = [];

    function getAuthToken() {
        return localStorage.getItem('jwt-token');
    }

    setupLogoutButton();
    await loadCurrentUser();

    if (createLeaveButton) {
        createLeaveButton.addEventListener('click', () => {
            window.location.href = '/create-leave';
        });
    }

    if (form) handleLeaveForm();
    if (leaveRequestsList) handleLeavesList();

    // -----------------------------------------------------
    // LOGOUT
    // -----------------------------------------------------
    function setupLogoutButton() {
        const logoutButton = document.querySelector('a[href="/auth/logout"]');
        if (logoutButton) {
            logoutButton.addEventListener('click', function (e) {
                e.preventDefault();
                localStorage.removeItem('jwt-token');
                window.location.href = '/login';
            });
        }
    }

    // -----------------------------------------------------
    // ROLE HELPERS
    // -----------------------------------------------------
    function hasRole(roleName) {
        if (!currentUser || !currentUser.roles) return false;
        // roles come as ["ADMIN", "EMPLOYEE", ...]
        return currentUser.roles.includes(roleName);
    }

    function isOwner(request) {
        return currentUser && request.employee && request.employee.id === currentUser.id;
    }

    function sameDepartment(request) {
        const reqDeptId = request?.employee?.department?.id;
        const userDeptId = currentUser?.department?.id;
        return reqDeptId != null && userDeptId != null && String(reqDeptId) === String(userDeptId);
    }


    // -----------------------------------------------------
    // LOAD CURRENT USER
    // -----------------------------------------------------
    async function loadCurrentUser() {
        try {
            const response = await fetch('/employees/current', {
                headers: { 'Authorization': 'Bearer ' + getAuthToken() }
            });

            if (!response.ok) return;

            const employee = await response.json();
            currentUser = employee;

            const userName = document.getElementById('userName');
            const userDept = document.getElementById('userDepartment');

            if (userName) {
                userName.textContent = employee.name + ' ' + employee.surname;
            }
            if (userDept) {
                userDept.textContent = employee.department?.name || 'No department';
            }

            const deptDisplay = document.getElementById('departmentDisplay');
            if (deptDisplay) {
                deptDisplay.textContent = employee.department?.name || 'No department';
            }

            const projectDisplay = document.getElementById('projectDisplay');
            if (projectDisplay) {
                projectDisplay.textContent =
                    employee.projects?.length > 0
                        ? employee.projects.map(p => p.name).join(', ')
                        : 'No projects assigned';
            }

        } catch (e) {
            console.error('Error loading current user:', e);
        }
    }

    // -----------------------------------------------------
    // CREATE / EDIT LEAVE FORM
    // -----------------------------------------------------
    function handleLeaveForm() {
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        const commentInput = document.getElementById('comment');
        const noPointsCheckbox = document.getElementById('noPoints');
        const errorMessage = document.getElementById('errorMessage');
        const successMessage = document.getElementById('successMessage');
        const cancelButton = document.getElementById('cancelButton');
        const title = document.getElementById('leaveFormTitle');
        const submitButton = document.getElementById('submitLeaveButton');
        const hiddenIdInput = document.getElementById('leaveRequestId');
        const pointsInfo = document.getElementById('pointsInfo');

        function showError(msg) {
            errorMessage.textContent = msg;
            errorMessage.classList.add('visible');
            successMessage.classList.remove('visible');
        }

        function showSuccess(msg) {
            successMessage.textContent = msg;
            successMessage.classList.add('visible');
            errorMessage.classList.remove('visible');
        }

        function updatePointsInfo() {
            const startDate = new Date(startDateInput.value);
            const endDate = new Date(endDateInput.value);
            const noPoints = noPointsCheckbox.checked;

            if (!startDateInput.value || !endDateInput.value || isNaN(startDate) || isNaN(endDate)) {
                pointsInfo.textContent = '';
                pointsError.style.display = 'none';
                return;
            }

            const days = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1;
            const balance = currentUser?.points ?? 0;

            if (noPoints) {
                pointsInfo.textContent = '';
                pointsError.style.display = 'none';
            } else {
                pointsInfo.textContent = `You will lose ${days} point${days > 1 ? 's' : ''} for this leave. Balance: ${balance}.`;

                pointsError.style.display = 'none'; // always hide during typing
            }
        }


        // Update when dates or checkbox change
        startDateInput.addEventListener('change', updatePointsInfo);
        endDateInput.addEventListener('change', updatePointsInfo);
        noPointsCheckbox.addEventListener('change', updatePointsInfo);


        // Detect edit mode from query param ?leaveId=...
        const params = new URLSearchParams(window.location.search);
        const leaveIdParam = params.get('leaveId');
        const isEditMode = !!leaveIdParam;

        if (isEditMode) {
            hiddenIdInput.value = leaveIdParam;
            if (title) title.textContent = 'Edit Leave Request';
            if (submitButton) submitButton.textContent = 'Update';

            loadExistingLeave(leaveIdParam);
        }

        async function loadExistingLeave(leaveId) {
            try {
                const response = await fetch(`/leaves/${leaveId}`, {
                    headers: {
                        'Authorization': 'Bearer ' + getAuthToken()
                    }
                });

                if (!response.ok) {
                    showError('Failed to load leave request for editing');
                    return;
                }

                const leave = await response.json();

                // Dates from backend are ISO strings; convert to yyyy-MM-dd
                if (leave.startDate) {
                    const d = new Date(leave.startDate);
                    startDateInput.value = d.toISOString().slice(0, 10);
                }
                if (leave.endDate) {
                    const d = new Date(leave.endDate);
                    endDateInput.value = d.toISOString().slice(0, 10);
                }

                commentInput.value = leave.comment || '';
                noPointsCheckbox.checked = !leave.usePoints;

            } catch (e) {
                console.error('Error loading leave for edit:', e);
                showError('Error loading leave request');
            }
        }

        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            if (isSubmitting) return;
            isSubmitting = true;

            const startDate = startDateInput.value;
            const endDate = endDateInput.value;

            if (new Date(startDate) > new Date(endDate)) {
                showError('End date must be after start date');
                isSubmitting = false;
                return;
            }

            if (new Date(startDate) > new Date(endDate)) {
                showError('End date must be after start date');
                isSubmitting = false;
                return;
            }

            const days = Math.ceil((new Date(endDate) - new Date(startDate)) / (1000 * 60 * 60 * 24)) + 1;
            const balance = currentUser?.points ?? 0;

            if (!noPointsCheckbox.checked && days > balance) {
                pointsError.textContent = `You do not have enough points to request this leave. Required: ${days}, Available: ${balance}`;
                pointsError.style.display = 'block';
                isSubmitting = false;
                return;
            }



            const requestData = {
                startDate: new Date(startDate).toISOString(),
                endDate: new Date(endDate).toISOString(),
                comment: commentInput.value,
                usePoints: !noPointsCheckbox.checked
            };

            const isEdit = !!hiddenIdInput.value;
            const url = isEdit ? `/leaves/${hiddenIdInput.value}` : '/leaves';
            const method = isEdit ? 'PUT' : 'POST';

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + getAuthToken()
                    },
                    body: JSON.stringify(requestData)
                });

                if (response.ok) {
                    pointsError.style.display = 'none';
                    showSuccess(isEdit ? 'Leave request updated successfully' : 'Leave request submitted successfully');
                    form.reset();
                    setTimeout(() => window.location.href = '/leaves-view', 800);
                } else {
                    const error = await response.json();
                    showError(error.message || 'Failed to submit leave request');
                }
            } catch (e) {
                showError('An error occurred while submitting the request');
            } finally {
                isSubmitting = false;
            }
        });

        cancelButton.addEventListener('click', () => {
            window.location.href = '/leaves-view';
        });
    }

    // -----------------------------------------------------
    // LEAVE LIST PAGE
    // -----------------------------------------------------
    function handleLeavesList() {
        const errorMessage = document.getElementById('error-message');

        function showError(msg) {
            errorMessage.textContent = msg;
            errorMessage.classList.add('visible');
        }

        function formatDate(dateString) {
            return new Date(dateString).toLocaleDateString('en-GB');
        }

        function getStatusClass(status) {
            return {
                APPROVED: 'status-approved',
                DECLINED: 'status-declined',
                PENDING: 'status-pending',
                CANCELLED: 'status-cancelled',
                MANUAL: 'status-manual'
            }[status] || '';
        }

        function buildActionsHtml(request) {
            const isEmp = hasRole('EMPLOYEE');
            const isMng = hasRole('MANAGER');
            const isAdm = hasRole('ADMIN');
            const owner = isOwner(request);
            const inactive = request.status !== 'PENDING';

            let buttons = [];

            // -----------------------------
            // ACTIVE (PENDING) REQUESTS
            // -----------------------------
            if (!inactive) {
                // Evaluate
                if ((isMng || isAdm) && !owner) {
                    buttons.push(`<button class="btn-evaluate" data-id="${request.id}">Evaluate</button>`);
                }

                // Edit/Delete for owner
                if (owner) {
                    buttons.push(`<button class="btn-edit" data-id="${request.id}">Edit</button>`);
                    buttons.push(`<button class="btn-delete" data-id="${request.id}">Delete</button>`);
                }

                // EMPLOYEE should not see Evaluate
                if (isEmp && !isMng && !isAdm) {
                    buttons = buttons.filter(b => !b.includes('btn-evaluate'));
                }

                return buttons.join(' ');
            }

            // -----------------------------
            // INACTIVE (RESOLVED) REQUESTS
            // -----------------------------
            // VIEW button rules:
            const managerEvaluated = request.manager && request.manager.id === currentUser.id;

            if (owner || managerEvaluated || isAdm) {
                buttons.push(`<button class="btn-edit" data-id="${request.id}">View</button>`);
            }

            // DELETE button rules:
            if (isAdm) {
                buttons.push(`<button class="btn-delete" data-id="${request.id}">Delete</button>`);
            }

            return buttons.join(' ');
        }


        function renderRequests(requests, isInactive) {
            return requests.map(request => `
                <div class="leave-request-item ${isInactive ? 'inactive' : ''}" data-id="${request.id}">
                    <div class="leave-request-header">
                        <span class="leave-request-id">#${request.id}</span>
                        <span class="leave-request-status ${getStatusClass(request.status)}">${request.status}</span>
                    </div>

                    <div class="leave-request-info">
                        <div class="info-row"><strong>From:</strong> ${formatDate(request.startDate)}</div>
                        <div class="info-row"><strong>To:</strong> ${formatDate(request.endDate)}</div>
                        <div class="info-row"><strong>Comment:</strong> ${request.comment || 'No comment'}</div>
                        <div class="info-row"><strong>Employee:</strong> ${request.employee?.name || ''} ${request.employee?.surname || ''}</div>
                        <div class="info-row"><strong>Department:</strong> ${request.employee?.department?.name || 'No department'}</div>
                    </div>

                    <div class="leave-request-actions">
                        ${buildActionsHtml(request)}
                    </div>
                </div>
            `).join('');
        }

        function applyFilters(requests) {
            let result = [...requests];

            if (filterMyRequests && currentUser) {
                result = result.filter(r => r.employee && r.employee.id === currentUser.id);
            }

            if (filterMyDepartment && currentUser && currentUser.department) {
                result = result.filter(r =>
                    r.employee &&
                    r.employee.department &&
                    r.employee.department.id === currentUser.department.id
                );
            }

            return result;
        }

        function renderAll() {
            const isEmpOnly = hasRole('EMPLOYEE') && !hasRole('MANAGER') && !hasRole('ADMIN');

            const filtered = applyFilters(allLeaveRequests);

            const sortedPending = filtered
                .filter(r => r.status === 'PENDING')
                .sort((a, b) => new Date(b.startDate) - new Date(a.startDate));

            const sortedOthers = filtered
                .filter(r => r.status !== 'PENDING')
                .sort((a, b) => new Date(b.startDate) - new Date(a.startDate));

            let html = '';

            html += `<h3>Pending Requests</h3>`;
            html += sortedPending.length
                ? renderRequests(sortedPending, false)
                : `<div class="no-requests">No pending requests</div>`;

            if (!isEmpOnly) {
                html += `<h3>Other Requests</h3>`;
                html += sortedOthers.length
                    ? renderRequests(sortedOthers, true)
                    : `<div class="no-requests">No other requests</div>`;
            }


            html += sortedOthers.length
                ? renderRequests(sortedOthers, true)
                : `<div class="no-requests">No other requests</div>`;

            leaveRequestsList.innerHTML = html;

            // Attach listeners
            leaveRequestsList.querySelectorAll('.btn-evaluate').forEach(button => {
                button.addEventListener('click', function (e) {
                    e.stopPropagation();
                    const id = this.dataset.id;
                    window.location.href = `/evaluate-leave-request/${id}`;
                });
            });

            leaveRequestsList.querySelectorAll('.btn-edit').forEach(button => {
                button.addEventListener('click', function (e) {
                    e.stopPropagation();
                    const id = this.dataset.id;
                    window.location.href = `/create-leave?leaveId=${id}`;
                });
            });

            leaveRequestsList.querySelectorAll('.btn-delete').forEach(button => {
                button.addEventListener('click', function (e) {
                    e.stopPropagation();
                    deleteTargetId = this.dataset.id;
                    openDeleteModal();
                });
            });

            leaveRequestsList.querySelectorAll('.leave-request-item').forEach(item => {
                item.addEventListener('click', function (e) {
                    // Clicking on empty space of card for managers/admins might navigate to evaluate;
                    // we keep it simple: only buttons do actions now.
                });
            });
        }

        function setupFilters() {
            if (!filtersContainer || !currentUser) return;

            const isMng = hasRole('MANAGER');
            const isAdm = hasRole('ADMIN');

            let html = '';

            if (isMng || isAdm) {
                html += `<label><input type="checkbox" id="filterMyRequests"> Show my requests only</label>`;
            }
            if (isAdm && currentUser.department) {
                html += `<label><input type="checkbox" id="filterMyDepartment"> Show my department only</label>`;
            }

            filtersContainer.innerHTML = html;

            const myReqCheckbox = document.getElementById('filterMyRequests');
            const myDeptCheckbox = document.getElementById('filterMyDepartment');

            if (myReqCheckbox) {
                myReqCheckbox.addEventListener('change', function () {
                    filterMyRequests = this.checked;
                    renderAll();
                });
            }
            if (myDeptCheckbox) {
                myDeptCheckbox.addEventListener('change', function () {
                    filterMyDepartment = this.checked;
                    renderAll();
                });
            }
        }

        async function loadLeaveRequests() {
            try {
                const response = await fetch('/leaves/all', {
                    headers: { 'Authorization': 'Bearer ' + getAuthToken() }
                });

                if (!response.ok) {
                    showError('Failed to load leave requests');
                    return;
                }

                allLeaveRequests = await response.json();

                setupFilters();
                renderAll();

            } catch (e) {
                console.error(e);
                showError('An error occurred while loading leave requests');
            }
        }

        // DELETE modal logic
        function openDeleteModal() {
            if (deleteModalOverlay) {
                deleteModalOverlay.classList.add('visible');
            }
        }

        function closeDeleteModal() {
            if (deleteModalOverlay) {
                deleteModalOverlay.classList.remove('visible');
            }
            deleteTargetId = null;
        }

        if (deleteCancelButton) {
            deleteCancelButton.addEventListener('click', function () {
                closeDeleteModal();
            });
        }

        if (deleteConfirmButton) {
            deleteConfirmButton.addEventListener('click', async function () {
                if (!deleteTargetId) return;

                try {
                    const response = await fetch(`/leaves/${deleteTargetId}`, {
                        method: 'DELETE',
                        headers: { 'Authorization': 'Bearer ' + getAuthToken() }
                    });

                    if (!response.ok) {
                        showError('Failed to delete leave request');
                    } else {
                        // remove from local list and rerender
                        allLeaveRequests = allLeaveRequests.filter(r => r.id != deleteTargetId);
                        renderAll();
                    }
                } catch (e) {
                    console.error(e);
                    showError('An error occurred while deleting the leave request');
                } finally {
                    closeDeleteModal();
                }
            });
        }

        loadLeaveRequests();
    }
});
