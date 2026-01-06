document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const leaveRequestId = urlParams.get('id');

    const approveButton = document.getElementById('approveButton');
    const rejectButton = document.getElementById('rejectButton');
    const evaluationComment = document.getElementById('evaluationComment');
    const errorMessage = document.getElementById('error-message');
    const successMessage = document.getElementById('success-message');
    const userNameDisplay = document.getElementById('userName');

    function getAuthToken() {
        return localStorage.getItem('jwt-token');
    }

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.add('visible');
        successMessage.classList.remove('visible');
        setTimeout(() => {
            errorMessage.classList.remove('visible');
        }, 5000);
    }

    function showSuccess(message) {
        successMessage.textContent = message;
        successMessage.classList.add('visible');
        errorMessage.classList.remove('visible');
        setTimeout(() => {
            successMessage.classList.remove('visible');
        }, 5000);
    }

    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' });
    }

    function calculateDays(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);
        const diffTime = end - start;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
        return diffDays > 0 ? diffDays : 0;
    }

    async function loadCurrentUser() {
        try {
            const response = await fetch('/employees/current', {
                headers: {
                    'Authorization': 'Bearer ' + getAuthToken()
                }
            });

            if (response.ok) {
                const employee = await response.json();
                if (userNameDisplay) {
                    userNameDisplay.textContent = employee.name + ' ' + employee.surname;
                }
            }
        } catch (error) {
            console.error('Error loading current user:', error);
            if (userNameDisplay) {
                userNameDisplay.textContent = 'Unknown User';
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
                headers: {
                    'Authorization': 'Bearer ' + getAuthToken()
                }
            });

            if (response.ok) {
                const leaveRequest = await response.json();
                
                const startDate = leaveRequest.startDate;
                const endDate = leaveRequest.endDate;
                const days = calculateDays(startDate, endDate);
                
                document.getElementById('detailStartDate').textContent = formatDate(startDate);
                document.getElementById('startDays').textContent = `(${days} day${days > 1 ? 's' : ''})`;
                document.getElementById('detailEndDate').textContent = formatDate(endDate);
                document.getElementById('endDays').textContent = `(${days} day${days > 1 ? 's' : ''})`;
                document.getElementById('detailReasoning').textContent = leaveRequest.comment || 'No reasoning provided';
                
                if (leaveRequest.employee && leaveRequest.employee.projects && leaveRequest.employee.projects.length > 0) {
                    const projectNames = leaveRequest.employee.projects.map(p => p.name).join(', ');
                    document.getElementById('detailProject').textContent = projectNames;
                } else {
                    document.getElementById('detailProject').textContent = 'No projects assigned';
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
            comment: comment || (approved ? 'Approved!' : 'Rejected!')
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
                    setTimeout(() => {
                        window.location.href = '/leaves';
                    }, 2000);
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

        modalOkButton.onclick = function() {
            modalOverlay.classList.remove('visible');
            window.location.href = '/leaves';
        };

        modalOverlay.onclick = function(e) {
            if (e.target === modalOverlay) {
                modalOverlay.classList.remove('visible');
                window.location.href = '/leaves';
            }
        };
    }

    approveButton.addEventListener('click', function() {
        evaluationComment.value = 'Approved!';
        evaluateLeaveRequest(true);
    });

    rejectButton.addEventListener('click', function() {
        evaluationComment.value = 'Rejected!';
        evaluateLeaveRequest(false);
    });

    loadCurrentUser();

    if (leaveRequestId) {
        loadLeaveRequestDetails();
    } else {
        showError('Please provide a leave request ID in the URL');
    }
});

