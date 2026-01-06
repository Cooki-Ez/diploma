document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('leaveForm');
    const leaveRequestsList = document.getElementById('leaveRequestsList');
    const createLeaveButton = document.getElementById('createLeaveButton');
    const userNameDisplay = document.getElementById('userName');

    if (form) {
        handleLeaveForm();
    }

    if (leaveRequestsList) {
        handleLeavesList();
    }

    if (createLeaveButton) {
        createLeaveButton.addEventListener('click', function() {
            window.location.href = '/create-leave';
        });
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
                const userNameElement = document.getElementById('userName') || document.querySelector('.user span');
                if (userNameElement) {
                    userNameElement.textContent = employee.name + ' ' + employee.surname;
                }
            }
        } catch (error) {
            console.error('Error loading current user:', error);
            const userNameElement = document.getElementById('userName') || document.querySelector('.user span');
            if (userNameElement) {
                userNameElement.textContent = 'Unknown User';
            }
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

        function formatDateForISO(dateString) {
            const date = new Date(dateString);
            return date.toISOString();
        }

        async function loadEmployeeData() {
            try {
                const response = await fetch('/employees/current', {
                    headers: {
                        'Authorization': 'Bearer ' + getAuthToken()
                    }
                });

                if (response.ok) {
                    const employee = await response.json();

                    const userNameElement = document.getElementById('userName') || document.querySelector('.user span');
                    if (userNameElement) {
                        userNameElement.textContent = employee.name + ' ' + employee.surname;
                    }

                    if (employee.projects && employee.projects.length > 0) {
                        const projectNames = employee.projects.map(p => p.name).join(', ');
                        projectDisplay.textContent = projectNames;
                    } else {
                        projectDisplay.textContent = 'No projects assigned';
                    }
                }
            } catch (error) {
                console.error('Error loading employee data:', error);
                projectDisplay.textContent = 'No projects assigned';
                const userNameElement = document.getElementById('userName') || document.querySelector('.user span');
                if (userNameElement) {
                    userNameElement.textContent = 'Unknown User';
                }
            }
        }

        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const startDate = startDateInput.value;
            const endDate = endDateInput.value;
            const comment = commentInput.value;
            const noPoints = noPointsCheckbox.checked;

            if (new Date(startDate) > new Date(endDate)) {
                showError('End date must be after start date');
                return;
            }

            const requestData = {
                startDate: formatDateForISO(startDate),
                endDate: formatDateForISO(endDate),
                comment: comment,
                usePoints: !noPoints
            };

            try {
                const response = await fetch('/leaves', {
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
                    setTimeout(() => {
                        window.location.href = '/leaves';
                    }, 1000);
                } else {
                    const error = await response.json();
                    showError(error.message || 'Failed to submit leave request');
                }
            } catch (error) {
                showError('An error occurred while submitting the request');
                console.error(error);
            }
        });

        cancelButton.addEventListener('click', function() {
            form.reset();
            window.location.href = '/leaves';
        });

        noPointsCheckbox.addEventListener('change', function() {
            console.log('Points waiver toggled:', this.checked);
        });

        loadEmployeeData();
    }

    function handleLeavesList() {
        const errorMessage = document.getElementById('error-message');

        function getAuthToken() {
            return localStorage.getItem('jwt-token');
        }

        function formatDate(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' });
        }

        function getStatusClass(status) {
            switch(status) {
                case 'APPROVED': return 'status-approved';
                case 'DECLINED': return 'status-declined';
                case 'PENDING': return 'status-pending';
                case 'CANCELLED': return 'status-cancelled';
                case 'MANUAL': return 'status-manual';
                default: return '';
            }
        }

        async function loadLeaveRequests() {
            try {
                const response = await fetch('/leaves', {
                    headers: {
                        'Authorization': 'Bearer ' + getAuthToken()
                    }
                });

                if (response.ok) {
                    const leaveRequests = await response.json();

                    if (leaveRequests.length === 0) {
                        leaveRequestsList.innerHTML = '<div class="no-requests">No leave requests found</div>';
                        return;
                    }

                    leaveRequestsList.innerHTML = leaveRequests.map(request => `
                        <div class="leave-request-item" data-id="${request.id}">
                            <div class="leave-request-header">
                                <span class="leave-request-id">#${request.id}</span>
                                <span class="leave-request-status ${getStatusClass(request.status)}">${request.status}</span>
                            </div>
                            <div class="leave-request-info">
                                <div class="info-row">
                                    <strong>From:</strong> ${formatDate(request.startDate)}
                                </div>
                                <div class="info-row">
                                    <strong>To:</strong> ${formatDate(request.endDate)}
                                </div>
                                <div class="info-row">
                                    <strong>Comment:</strong> ${request.comment || 'No comment'}
                                </div>
                                ${request.employee ? `<div class="info-row"><strong>Employee:</strong> ${request.employee.name} ${request.employee.surname}</div>` : ''}
                            </div>
                            <div class="leave-request-actions">
                                <button class="btn-evaluate" data-id="${request.id}">Evaluate</button>
                            </div>
                        </div>
                    `).join('');

                    document.querySelectorAll('.btn-evaluate').forEach(button => {
                        button.addEventListener('click', function() {
                            const requestId = this.getAttribute('data-id');
                            window.location.href = `/evaluate-leave-request?id=${requestId}`;
                        });
                    });

                    document.querySelectorAll('.leave-request-item').forEach(item => {
                        item.addEventListener('click', function(e) {
                            if (!e.target.classList.contains('btn-evaluate')) {
                                const requestId = this.getAttribute('data-id');
                                window.location.href = `/evaluate-leave-request?id=${requestId}`;
                            }
                        });
                    });

                } else {
                    showError('Failed to load leave requests');
                }
            } catch (error) {
                showError('An error occurred while loading leave requests');
                console.error(error);
            }
        }

        function showError(message) {
            errorMessage.textContent = message;
            errorMessage.classList.add('visible');
            setTimeout(() => {
                errorMessage.classList.remove('visible');
            }, 5000);
        }

        loadLeaveRequests();
    }

    loadCurrentUser();
});
