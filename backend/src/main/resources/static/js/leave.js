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
    let myLeaveRequests = [];

    function datesOverlap(start1, end1, start2, end2) {
        return start1 < end2 && start2 < end1;
    }

    function hasOverlappingRequest(startDate, endDate) {
        const hiddenIdInput = document.getElementById('leaveRequestId');
        const currentId = hiddenIdInput ? Number(hiddenIdInput.value) : null;

        return myLeaveRequests.some(req => {
            if (currentId && req.id === currentId) {
                return false;
            }
            const reqStart = new Date(req.startDate);
            const reqEnd = new Date(req.endDate);
            return datesOverlap(startDate, endDate, reqStart, reqEnd);
        });
    }

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

    if (form) {
        await loadCurrentUser();

        const myResp = await fetch('/leaves/my', {
            headers: { 'Authorization': 'Bearer ' + getAuthToken() }
        });
        if (myResp.ok) {
            myLeaveRequests = await myResp.json();
        }

        await handleLeaveForm();
    }

    if (leaveRequestsList) handleLeavesList();

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

    function hasRole(roleName) {
        if (!currentUser || !currentUser.roles) return false;
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
    // WORKING DAYS CALCULATOR (Mon–Fri only)
    // -----------------------------------------------------
    function calculateWorkingDays(startDate, endDate) {
        let count = 0;
        let date = new Date(startDate);

        while (date <= endDate) {
            const day = date.getDay(); // 0=Sun, 6=Sat
            if (day !== 0 && day !== 6) {
                count++;
            }
            date.setDate(date.getDate() + 1);
        }
        return count;
    }

    async function handleLeaveForm() {
        await loadCurrentUser();
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
        const pointsError = document.getElementById('pointsError');
        let isEvaluated = false;

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
            if (isEvaluated) {
                pointsInfo.textContent = '';
                pointsError.style.display = 'none';
                errorMessage.classList.remove('visible');
                return;
            }

            errorMessage.classList.remove('visible');
            pointsError.style.display = 'none';
            successMessage.classList.remove('visible');

            const startDate = new Date(startDateInput.value);
            const endDate = new Date(endDateInput.value);
            const noPoints = noPointsCheckbox.checked;

            if (!startDateInput.value || !endDateInput.value || isNaN(startDate) || isNaN(endDate)) {
                pointsInfo.textContent = '';
                pointsError.style.display = 'none';
                return;
            }

            if (hasOverlappingRequest(startDate, endDate)) {
                errorMessage.textContent = 'You already have a leave request during these dates.';
                errorMessage.classList.add('visible');
                return;
            }

            const days = calculateWorkingDays(startDate, endDate);
            const balance = currentUser?.points ?? 0;

            if (noPoints) {
                pointsInfo.textContent = '';
                pointsError.style.display = 'none';
            } else {
                pointsInfo.textContent = `You will lose ${days} point${days > 1 ? 's' : ''} for this leave. Balance: ${balance}.`;
                pointsError.style.display = 'none';
            }
        }

        startDateInput.addEventListener('change', updatePointsInfo);
        endDateInput.addEventListener('change', updatePointsInfo);
        noPointsCheckbox.addEventListener('change', updatePointsInfo);

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
                const response = await fetch(`/leaves/${leaveId}?t=${Date.now()}`, {
                    headers: {
                        'Authorization': 'Bearer ' + getAuthToken()
                    }
                });

                if (!response.ok) {
                    showError('Failed to load leave request for editing');
                    return;
                }

                const leave = await response.json();

                isEvaluated = leave.status !== 'PENDING';

                if (leave.startDate) {
                    startDateInput.value = leave.startDate.slice(0, 10);
                }
                if (leave.endDate) {
                    endDateInput.value = leave.endDate.slice(0, 10);
                }

                commentInput.value = leave.comment || '';
                noPointsCheckbox.checked = !leave.usePoints;
                if (!isEvaluated) {
                    noPointsCheckbox.dispatchEvent(new Event('change'));
                }


                const managerRow = document.getElementById('managerRow');
                const managerDisplay = document.getElementById('managerDisplay');

                const managerCommentRow = document.getElementById('managerCommentRow');
                const managerCommentDisplay = document.getElementById('managerCommentDisplay');

                const isAdmin = currentUser?.roles?.includes('ADMIN');

                if (isEvaluated && !isAdmin) {
                    startDateInput.disabled = true;
                    endDateInput.disabled = true;
                    commentInput.disabled = true;
                    noPointsCheckbox.disabled = true;
                    submitButton.style.display = 'none';
                    cancelButton.textContent = 'Back';
                }

                if (managerRow && managerDisplay) {
                    if (isEvaluated && leave.managerName) {
                        managerDisplay.textContent = `${leave.managerName} ${leave.managerSurname}`;
                        managerRow.style.display = 'flex';
                    } else {
                        managerRow.style.display = 'none';
                    }
                }

                if (managerCommentRow && managerCommentDisplay) {
                    if (isEvaluated && leave.evaluationComment) {
                        managerCommentDisplay.textContent = leave.evaluationComment;
                        managerCommentRow.style.display = 'flex';
                    } else {
                        managerCommentRow.style.display = 'none';
                    }
                }

            } catch (e) {
                console.error('Error loading leave for edit:', e);
                showError('Error loading leave request');
            }
        }


        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            if (isEvaluated) {
                showError('This leave request has already been evaluated and cannot be changed.');
                return;
            }

            if (isSubmitting) return;
            isSubmitting = true;

            const startDate = new Date(startDateInput.value);
            const endDate = new Date(endDateInput.value);

            if (startDate > endDate) {
                showError('End date must be after start date');
                isSubmitting = false;
                return;
            }

           const today = new Date();
           today.setHours(0, 0, 0, 0);
           if (endDate < today) {
                showError('End date cannot be in the past');
                isSubmitting = false;
                return;
           }

           if (startDate < today) {
                showError('Start date cannot be in the past');
                isSubmitting = false;
                return;
           }

           if (hasOverlappingRequest(startDate, endDate)) {
               showError('You already have a leave request during these dates.');
               isSubmitting = false;
               return;
           }


            const days = calculateWorkingDays(startDate, endDate);
            const balance = currentUser?.points ?? 0;

            if (!noPointsCheckbox.checked && days > balance) {
                pointsError.textContent = `You do not have enough points to request this leave. Required: ${days}, Available: ${balance}`;
                pointsError.style.display = 'block';
                isSubmitting = false;
                return;
            }

            const requestData = {
                startDate: startDate.toISOString(),
                endDate: endDate.toISOString(),
                usePoints: !noPointsCheckbox.checked
            };

            const evaluationCommentInput = document.getElementById('evaluationCommentInput');
            if (evaluationCommentInput) {
                requestData.evaluationComment = evaluationCommentInput.value;
            }

            const employeeCommentInput = document.getElementById('comment');
            if (employeeCommentInput) {
                requestData.employeeComment = employeeCommentInput.value;
            }


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

            const sameDept =
                request.employee?.department?.id &&
                currentUser?.department?.id &&
                request.employee.department.id === currentUser.department.id;

            let buttons = [];

            if (!inactive) {

                if (isAdm && !owner) {
                    buttons.push(`<button class="btn-evaluate" data-id="${request.id}">Evaluate</button>`);
                }

                // Managers/Admins can evaluate requests from their department
                if (isMng && !owner && sameDept) {
                    buttons.push(`<button class="btn-evaluate" data-id="${request.id}">Evaluate</button>`);
                }

                if (owner) {
                    buttons.push(`<button class="btn-edit" data-id="${request.id}">Edit</button>`);
                    buttons.push(`<button class="btn-delete" data-id="${request.id}">Delete</button>`);
                }

                if (isEmp && !isMng && !isAdm) {
                    buttons = buttons.filter(b => !b.includes('btn-evaluate'));
                }

                // Admins can edit/delete any pending request
                if (isAdm) {
                    buttons.push(`<button class="btn-edit" data-id="${request.id}">Edit</button>`);
                    buttons.push(`<button class="btn-delete" data-id="${request.id}">Delete</button>`);
                }

                return buttons.join(' ');
            }

            const managerEvaluated =
                request.manager &&
                request.manager.id === currentUser.id;

            if (owner || managerEvaluated || isMng || isAdm) {
                const label = isAdm && inactive ? 'Edit' : 'View';
                buttons.push(`<button class="btn-edit" data-id="${request.id}">${label}</button>`);
            }

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
                        <div class="info-row"><strong>Manager:</strong> ${request.managerName || '—'} ${request.managerSurname || ''}</div>
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


                html += `<h3>Other Requests</h3>`;
                html += sortedOthers.length
                    ? renderRequests(sortedOthers, true)
                    : `<div class="no-requests">No other requests</div>`;


            leaveRequestsList.innerHTML = html;

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
                const response = await fetch('/leaves/all?t=' + Date.now(), {
                    headers: { 'Authorization': 'Bearer ' + getAuthToken() },
                    cache: 'no-store'
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
