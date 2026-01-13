document.addEventListener('DOMContentLoaded', function() {
    const leaveRequestId = document.getElementById('leaveRequestId').value;

    const approveButton = document.getElementById('approveButton');
    const rejectButton = document.getElementById('rejectButton');
    const evaluationComment = document.getElementById('evaluationComment');
    const errorMessage = document.getElementById('error-message');
    const successMessage = document.getElementById('success-message');
    const userNameDisplay = document.getElementById('userName');

    function getAuthToken() {
        return localStorage.getItem('jwt-token');
    }

    setupLogoutButton();

    async function handleLogout() {
        try {
            await fetch('/auth/logout', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + getAuthToken()
                }
            });

            localStorage.removeItem('jwt-token');
            window.location.href = '/login';
        } catch (error) {
            console.error('Logout error:', error);
            localStorage.removeItem('jwt-token');
            window.location.href = '/login';
        }
    }

    function setupLogoutButton() {
        const logoutButton = document.querySelector('a[href="/auth/logout"]');
        if (logoutButton) {
            logoutButton.addEventListener('click', function(e) {
                e.preventDefault();
                handleLogout();
            });
        }
    }

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.add('visible');
        successMessage.classList.remove('visible');
        setTimeout(() => errorMessage.classList.remove('visible'), 5000);
    }

    function showSuccess(message) {
        successMessage.textContent = message;
        successMessage.classList.add('visible');
        errorMessage.classList.remove('visible');
        setTimeout(() => successMessage.classList.remove('visible'), 5000);
    }

    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' });
    }

    function calculateDays(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);
        const diffTime = end.getTime() - start.getTime();
        const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;
        return diffDays > 0 ? diffDays : 0;
    }

    async function loadCurrentUser() {
        try {
            const response = await fetch('/employees/current', {
                headers: { 'Authorization': 'Bearer ' + getAuthToken() }
            });
            if (response.ok) {
                const employee = await response.json();
                userNameDisplay.textContent = `${employee.name} ${employee.surname}`;

                const userDepartment = document.getElementById('userDepartment');
                if (userDepartment) {
                    userDepartment.textContent = employee.department?.name || 'No department';
                }
            }
        } catch (error) {
            console.error('Error loading current user:', error);
            userNameDisplay.textContent = 'Unknown User';
            const userDepartment = document.getElementById('userDepartment');
            if (userDepartment) {
                userDepartment.textContent = 'No department';
            }
        }
    }

    async function loadLeaveRequestDetails() {
        if (!leaveRequestId) {
            showError('Leave request ID is required');
            return;
        }

        try {
            const response = await fetch(`/leaves/${leaveRequestId}`, {
                headers: { 'Authorization': 'Bearer ' + getAuthToken() }
            });

            if (response.ok) {
                const leaveRequest = await response.json();

                const startDate = leaveRequest.startDate;
                const endDate = leaveRequest.endDate;
                const days = calculateDays(startDate, endDate);

                document.getElementById('detailEmployee').textContent =
                    leaveRequest.employee?.name && leaveRequest.employee?.surname
                        ? `${leaveRequest.employee.name} ${leaveRequest.employee.surname}`
                        : 'Unknown';

                document.getElementById('detailStartDate').textContent = formatDate(startDate);
                document.getElementById('startDays').textContent = `(${days} day${days > 1 ? 's' : ''})`;
                document.getElementById('detailEndDate').textContent = formatDate(endDate);

                if (leaveRequest.creationDate) {
                    document.getElementById('detailCreationDate').textContent = formatDate(leaveRequest.creationDate);
                }

                document.getElementById('detailReasoning').textContent =
                    leaveRequest.comment?.trim() || 'No reasoning provided';

                const projects = leaveRequest.employee?.projects;
                if (Array.isArray(projects) && projects.length > 0) {
                    const projectNames = projects.map(p => p.name).join(', ');
                    document.getElementById('detailProject').textContent = projectNames;
                } else {
                    document.getElementById('detailProject').textContent = 'No projects assigned';
                }

                if (leaveRequest.evaluatedBy) {
                    document.getElementById('detailEvaluatedBy').textContent =
                        `${leaveRequest.evaluatedBy.name || ''} ${leaveRequest.evaluatedBy.surname || ''}`.trim();
                    document.getElementById('evaluatedByRow').style.display = 'flex';
                }

                if (leaveRequest.evaluationComment) {
                    document.getElementById('detailEvaluationComment').textContent = leaveRequest.evaluationComment;
                    document.getElementById('evaluationCommentRow').style.display = 'flex';
                }

                if (leaveRequest.employee?.department?.name) {
                    document.getElementById('detailDepartment').textContent = leaveRequest.employee.department.name;
                } else {
                    document.getElementById('detailDepartment').textContent = 'No department';
                }

                if (leaveRequest.status !== 'PENDING') {
                    evaluationComment.value = leaveRequest.evaluationComment || '';
                    approveButton.disabled = true;
                    rejectButton.disabled = true;
                } else {
                    evaluationComment.value = '';
                    approveButton.disabled = false;
                    rejectButton.disabled = false;
                }
            } else {
                showError('Failed to load leave request details');
            }
        } catch (error) {
            showError('An error occurred while loading leave request details');
            console.error(error);
        }
    }

    async function evaluateLeaveRequest(approved) {
        const comment = evaluationComment.value.trim();
        const requestData = {
            status: approved ? 'APPROVED' : 'DECLINED',
            evaluationComment: comment || (approved ? 'Approved!' : 'Rejected!')
        };

        try {
            const response = await fetch(`/leaves/${leaveRequestId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + getAuthToken()
                },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                if (approved) {
                    showModal();
                } else {
                    showSuccess('Leave request rejected!');
                    setTimeout(() => window.location.href = '/leaves-view', 2000);
                }
            } else {
                const error = await response.json();
                showError(error.message || 'Failed to evaluate leave request');
            }
        } catch (error) {
            showError('An error occurred while evaluating the request');
            console.error(error);
        }
    }

    function showModal() {
        const modalOverlay = document.getElementById('modalOverlay');
        const modalOkButton = document.getElementById('modalOkButton');

        modalOverlay.classList.add('visible');

        modalOkButton.onclick = () => {
            modalOverlay.classList.remove('visible');
            window.location.href = '/leaves-view';
        };

        modalOverlay.onclick = (e) => {
            if (e.target === modalOverlay) {
                modalOverlay.classList.remove('visible');
                window.location.href = '/leaves-view';
            }
        };
    }

    approveButton.addEventListener('click', () => {
        evaluateLeaveRequest(true);
    });

    rejectButton.addEventListener('click', () => {
        evaluateLeaveRequest(false);
    });

    loadCurrentUser();
    loadLeaveRequestDetails();
});
