document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const leaveForm = document.getElementById('leaveForm');

    const API_BASE_URL = 'http://localhost:8080';

    setupLogoutButton(API_BASE_URL);

    if (loginForm) {
        handleLoginForm();
    }

    if (leaveForm) {
        handleLeaveForm();
    }

    function getAuthToken() {
        return localStorage.getItem('jwt-token');
    }

    async function handleLogout() {
        try {
            await fetch(`${API_BASE_URL}/auth/logout`, {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + getAuthToken()
                }
            });

            localStorage.removeItem('jwt-token');
            window.location.href = '/login.html';
        } catch (error) {
            console.error('Logout error:', error);
            localStorage.removeItem('jwt-token');
            window.location.href = '/login.html';
        }
    }

    function setupLogoutButton(apiBaseUrl) {
        const logoutButton = document.getElementById('logoutButton');
        if (logoutButton) {
            logoutButton.addEventListener('click', function(e) {
                e.preventDefault();
                handleLogout();
            });
        }
    }

    function handleLoginForm() {
        const loginForm = document.getElementById('loginForm');
        const errorMessage = document.getElementById('error-message');

        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch(`${API_BASE_URL}/auth/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();

                if (response.ok && data['jwt-token']) {
                    localStorage.setItem('jwt-token', data['jwt-token']);
                    window.location.href = '/create-leave.html';
                } else {
                    showError(data.message || 'Login failed');
                }
            } catch (error) {
                showError('An error occurred during login');
                console.error(error);
            }
        });

        function showError(message) {
            errorMessage.textContent = message;
            errorMessage.classList.add('visible');
            setTimeout(() => {
                errorMessage.classList.remove('visible');
            }, 5000);
        }
    }

    function handleLeaveForm() {
        const form = document.getElementById('leaveForm');
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        const commentInput = document.getElementById('comment');
        const noPointsCheckbox = document.getElementById('noPoints');
        const projectDisplay = document.getElementById('projectDisplay');
        const errorMessage = document.getElementById('errorMessage');
        const successMessage = document.getElementById('successMessage');
        const cancelButton = document.getElementById('cancelButton');
        const userNameDisplay = document.getElementById('userName');

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
                const response = await fetch(`${API_BASE_URL}/employees/current`, {
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
                    setTimeout(() => {
                        window.location.href = '/leaves.html';
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
            window.location.href = '/leaves.html';
        });

        noPointsCheckbox.addEventListener('change', function() {
            console.log('Points waiver toggled:', this.checked);
        });

        loadEmployeeData();
    }
});
