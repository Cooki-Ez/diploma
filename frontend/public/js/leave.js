document.addEventListener('DOMContentLoaded', function () {

    const API_BASE_URL = 'http://localhost:8080';

    const form = document.getElementById('leaveForm');
    const leaveRequestsList = document.getElementById('leaveRequestsList');
    const createLeaveButton = document.getElementById('createLeaveButton');
    const userNameDisplay = document.getElementById('userName');

    let isSubmitting = false;

    function getAuthToken() {
        return localStorage.getItem('jwt-token');
    }

    setupLogoutButton(API_BASE_URL);
    loadCurrentUser(API_BASE_URL);

    if (form) handleLeaveForm();
    if (leaveRequestsList) handleLeavesList();
    if (createLeaveButton) {
        createLeaveButton.addEventListener('click', () => {
            window.location.href = '/create-leave.html';
        });
    }

    function setupLogoutButton(apiBaseUrl) {
        const logoutButton = document.getElementById('logoutButton');
        if (logoutButton) {
            logoutButton.addEventListener('click', function (e) {
                e.preventDefault();
                handleLogout(apiBaseUrl);
            });
        }
    }

    async function handleLogout(apiBaseUrl) {
        try {
            await fetch(`${apiBaseUrl}/auth/logout`, {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + getAuthToken() }
            });
        } catch (e) {
            console.error('Logout error:', e);
        }
        localStorage.removeItem('jwt-token');
        window.location.href = '/login.html';
    }

    async function loadCurrentUser(apiBaseUrl) {
        try {
            const response = await fetch(`${apiBaseUrl}/employees/current`, {
                headers: { 'Authorization': 'Bearer ' + getAuthToken() }
            });

            if (response.ok) {
                const employee = await response.json();
                userNameDisplay.textContent = employee.name + ' ' + employee.surname;
            }
        } catch (e) {
            console.error('Error loading current user:', e);
            userNameDisplay.textContent = 'Unknown User';
        }
    }

    function handleLeaveForm() {
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        const commentInput = document.getElementById('comment');
        const noPointsCheckbox = document.getElementById('noPoints');
        const projectDisplay = document.getElementById('projectDisplay');
        const errorMessage = document.getElementById('errorMessage');
        const successMessage = document.getElementById('successMessage');
        const cancelButton = document.getElementById('cancelButton');

        function showError(msg) {
            errorMessage.textContent = msg;
            errorMessage.classList.add('visible');
            successMessage.classList.remove('visible');
            setTimeout(() => errorMessage.classList.remove('visible'), 5000);
        }

        function showSuccess(msg) {
            successMessage.textContent = msg;
            successMessage.classList.add('visible');
            errorMessage.classList.remove('visible');
            setTimeout(() => successMessage.classList.remove('visible'), 5000);
        }

        async function loadEmployeeData() {
            try {
                const response = await fetch(`${API_BASE_URL}/employees/current`, {
                    headers: { 'Authorization': 'Bearer ' + getAuthToken() }
                });

                if (response.ok) {
                    const employee = await response.json();
                    userNameDisplay.textContent = employee.name + ' ' + employee.surname;

                    if (employee.projects?.length > 0) {
                        projectDisplay.textContent = employee.projects.map(p => p.name).join(', ');
                    } else {
                        projectDisplay.textContent = 'No projects assigned';
                    }
                }
            } catch (e) {
                console.error('Error loading employee data:', e);
                projectDisplay.textContent = 'No projects assigned';
            }
        }

        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            if (isSubmitting) return;
            isSubmitting = true;

            const startDate = startDateInput.value;
            const endDate = endDateInput.value;
            const comment = commentInput.value;
            const noPoints = noPointsCheckbox.checked;

            if (new Date(startDate) > new Date(endDate)) {
                showError('End date must be after start date');
                isSubmitting = false;
                return;
            }

            const requestData = {
                startDate: new Date(startDate).toISOString(),
                endDate: new Date(endDate).toISOString(),
                comment: comment,
                usePoints: !noPoints
            };

            try {
                const response = await fetch(`${API_BASE_URL}/leaves`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + getAuthToken()
                    },
                    body: JSON.stringify(requestData)
                });

                if (response.ok) {
                    showSuccess('Leave request submitted successfully');
                    form.reset();
                    setTimeout(() => window.location.href = '/leaves.html', 1000);
                } else {
                    const error = await response.json();
                    showError(error.message || 'Failed to submit leave request');
                }
            } catch (e) {
                showError('An error occurred while submitting the request');
                console.error(e);
            } finally {
                isSubmitting = false;
            }
        });

        cancelButton.addEventListener('click', () => {
            form.reset();
            window.location.href = '/leaves.html';
        });

        loadEmployeeData();
    }

    function handleLeavesList() {
        const errorMessage = document.getElementById('error-message');

        function showError(msg) {
            errorMessage.textContent = msg;
            errorMessage.classList.add('visible');
            setTimeout(() => errorMessage.classList.remove('visible'), 5000);
        }

        function formatDate(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('en-GB', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            });
        }

        function getStatusClass(status) {
            switch (status) {
                case 'APPROVED': return 'status-approved';
                case 'DECLINED': return 'status-declined';
                case 'PENDING': return 'status-pending';
                case 'CANCELLED': return 'status-cancelled';
                case 'MANUAL': return 'status-manual';
                default: return '';
            }
        }

        function renderRequests(requests) {
            return requests.map(request => `
                <div class="leave-request-item" data-id="${request.id}">
                    <div class="leave-request-header">
                        <span class="leave-request-id">#${request.id}</span>
                        <span class="leave-request-status ${getStatusClass(request.status)}">${request.status}</span>
                    </div>
                    <div class="leave-request-info">
                        <div class="info-row"><strong>From:</strong> ${formatDate(request.startDate)}</div>
                        <div class="info-row"><strong>To:</strong> ${formatDate(request.endDate)}</div>
                        <div class="info-row"><strong>Created:</strong> ${formatDate(request.creationDate)}</div>
                        <div class="info-row"><strong>Comment:</strong> ${request.comment || 'No comment'}</div>
                        <div class="info-row"><strong>Employee:</strong> ${request.employee?.name || ''} ${request.employee?.surname || ''}</div>
                    </div>
                    <div class="leave-request-actions">
                        <button class="btn-evaluate" data-id="${request.id}">Evaluate</button>
                    </div>
                </div>
            `).join('');
        }

        async function loadLeaveRequests() {
            try {
                const response = await fetch(`${API_BASE_URL}/leaves`, {
                    headers: { 'Authorization': 'Bearer ' + getAuthToken() }
                });

                if (!response.ok) {
                    showError('Failed to load leave requests');
                    return;
                }

                const leaveRequests = await response.json();

                if (leaveRequests.length === 0) {
                    leaveRequestsList.innerHTML = '<div class="no-requests">No leave requests found</div>';
                    return;
                }

                leaveRequestsList.innerHTML = renderRequests(leaveRequests);

                document.querySelectorAll('.btn-evaluate').forEach(button => {
                    button.addEventListener('click', function () {
                        const id = this.getAttribute('data-id');
                        window.location.href = `/evaluate-leave-request.html?id=${id}`;
                    });
                });

                document.querySelectorAll('.leave-request-item').forEach(item => {
                    item.addEventListener('click', function (e) {
                        if (!e.target.classList.contains('btn-evaluate')) {
                            const id = this.getAttribute('data-id');
                            window.location.href = `/evaluate-leave-request.html?id=${id}`;
                        }
                    });
                });

            } catch (e) {
                showError('An error occurred while loading leave requests');
                console.error(e);
            }
        }

        loadLeaveRequests();
    }
});
