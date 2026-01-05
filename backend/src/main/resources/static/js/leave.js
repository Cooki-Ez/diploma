document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('leaveForm');
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
                
                document.querySelector('.user span').textContent = employee.fullName || 'Unknown User';

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
        window.location.href = '/login';
    });

    noPointsCheckbox.addEventListener('change', function() {
        console.log('Points waiver toggled:', this.checked);
    });

    loadEmployeeData();
});
