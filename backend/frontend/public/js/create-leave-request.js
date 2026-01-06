document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('leaveRequestForm');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const usePointsCheckbox = document.getElementById('usePoints');
    const pointsInfo = document.getElementById('pointsInfo');
    const pointsCount = document.getElementById('pointsCount');
    const projectDisplay = document.getElementById('projectDisplay');
    const errorMessage = document.getElementById('error-message');
    const successMessage = document.getElementById('success-message');
    const cancelButton = document.getElementById('cancelButton');

    let userPoints = 0;

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

    function calculateDays(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);
        const diffTime = end - start;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
        return diffDays > 0 ? diffDays : 0;
    }

    function updatePointsDisplay() {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;

        if (!usePointsCheckbox.checked && startDate && endDate) {
            const days = calculateDays(startDate, endDate);
            pointsCount.textContent = days;
            pointsInfo.classList.add('visible');
        } else {
            pointsInfo.classList.remove('visible');
        }
    }

    async function loadUserData() {
        try {
            const response = await fetch('/employees', {
                headers: {
                    'Authorization': 'Bearer ' + getAuthToken()
                }
            });

            if (response.ok) {
                const employees = await response.json();
                const currentEmployee = employees[0];
                
                if (currentEmployee) {
                    userPoints = currentEmployee.points || 0;
                    
                    if (currentEmployee.projects && currentEmployee.projects.length > 0) {
                        const projectNames = currentEmployee.projects.map(p => p.name).join(', ');
                        projectDisplay.textContent = projectNames;
                    } else {
                        projectDisplay.textContent = 'No projects assigned';
                    }
                }
            }
        } catch (error) {
            console.error('Error loading user data:', error);
        }
    }

    startDateInput.addEventListener('change', updatePointsDisplay);
    endDateInput.addEventListener('change', updatePointsDisplay);
    usePointsCheckbox.addEventListener('change', updatePointsDisplay);

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        const comment = document.getElementById('comment').value;
        const usePoints = !usePointsCheckbox.checked;

        if (new Date(startDate) > new Date(endDate)) {
            showError('End date must be after start date');
            return;
        }

        const requestData = {
            startDate: new Date(startDate).toISOString(),
            endDate: new Date(endDate).toISOString(),
            comment: comment,
            usePoints: usePoints
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
                pointsInfo.classList.remove('visible');
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
        pointsInfo.classList.remove('visible');
        window.location.href = '/login';
    });

    loadUserData();
});
