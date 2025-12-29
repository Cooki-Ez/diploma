// Create Leave Request Page JavaScript
// Handles leave request creation, user data retrieval, and point calculation

// DOM Elements
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('leaveRequestForm');  // Leave request form
    const startDateInput = document.getElementById('startDate');  // Start date input
    const endDateInput = document.getElementById('endDate');  // End date input
    const usePointsCheckbox = document.getElementById('usePoints');  // "Do not use points" checkbox
    const pointsInfo = document.getElementById('pointsInfo');  // Points info display div
    const pointsCount = document.getElementById('pointsCount');  // Points count span
    const projectDisplay = document.getElementById('projectDisplay');  // Project display div
    const errorMessage = document.getElementById('error-message');  // Error message display
    const successMessage = document.getElementById('success-message');  // Success message display
    const cancelButton = document.getElementById('cancelButton');  // Cancel button

    let userPoints = 0;  // Variable to store user's current points

    // ========== HELPER: GET AUTH TOKEN ==========
    // Retrieves JWT token from localStorage
    // Used for all authenticated API requests
    function getAuthToken() {
        return localStorage.getItem('jwt-token');
    }

    // ========== HELPER: SHOW ERROR ==========
    // Displays error message and auto-hides after 5 seconds
    function showError(message) {
        errorMessage.textContent = message;  // Set error message text
        errorMessage.classList.add('visible');  // Make error visible
        successMessage.classList.remove('visible');  // Hide success message
        setTimeout(() => {
            errorMessage.classList.remove('visible');  // Auto-hide after 5 seconds
        }, 5000);
    }

    // ========== HELPER: SHOW SUCCESS ==========
    // Displays success message and auto-hides after 5 seconds
    function showSuccess(message) {
        successMessage.textContent = message;  // Set success message text
        successMessage.classList.add('visible');  // Make success visible
        errorMessage.classList.remove('visible');  // Hide error message
        setTimeout(() => {
            successMessage.classList.remove('visible');  // Auto-hide after 5 seconds
        }, 5000);
    }

    // ========== HELPER: CALCULATE DAYS ==========
    // Calculates the number of days between start and end date
    // Logic: (end - start) in milliseconds / milliseconds per day + 1 (inclusive)
    function calculateDays(startDate, endDate) {
        const start = new Date(startDate);  // Parse start date
        const end = new Date(endDate);  // Parse end date
        const diffTime = end - start;  // Difference in milliseconds
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;  // Convert to days
        return diffDays > 0 ? diffDays : 0;  // Return 0 if negative
    }

    // ========== METHOD: UPDATE POINTS DISPLAY ==========
    // Shows/hides the points usage information based on checkbox and dates
    // Triggered when start date, end date, or checkbox changes
    function updatePointsDisplay() {
        const startDate = startDateInput.value;  // Get start date value
        const endDate = endDateInput.value;  // Get end date value

        // Show points if:
        // 1. Checkbox is NOT checked (meaning user WILL use points)
        // 2. Both dates are selected
        if (!usePointsCheckbox.checked && startDate && endDate) {
            const days = calculateDays(startDate, endDate);  // Calculate days
            pointsCount.textContent = days;  // Display day count
            pointsInfo.classList.add('visible');  // Show points info div
        } else {
            pointsInfo.classList.remove('visible');  // Hide points info div
        }
    }

    // ========== METHOD: LOAD USER DATA ==========
    // Retrieves current logged-in employee data from backend
    // This is WHERE THE CURRENT EMPLOYEE IS RETRIEVED for this page
    // Called on page load
    async function loadUserData() {
        try {
            // GET request to /employees endpoint with JWT token
            const response = await fetch('/employees', {
                headers: {
                    'Authorization': 'Bearer ' + getAuthToken()  // Include JWT token
                }
            });

            if (response.ok) {
                const employees = await response.json();  // Parse response as JSON
                const currentEmployee = employees[0];  // Get first employee (current user)

                if (currentEmployee) {
                    // Store user's points balance for validation/display
                    userPoints = currentEmployee.points || 0;

                    // Display user's assigned projects
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

    // ========== EVENT LISTENERS FOR POINTS DISPLAY ==========
    // Update points display when dates or checkbox change
    startDateInput.addEventListener('change', updatePointsDisplay);  // Start date change
    endDateInput.addEventListener('change', updatePointsDisplay);  // End date change
    usePointsCheckbox.addEventListener('change', updatePointsDisplay);  // Checkbox change

    // ========== FORM SUBMIT HANDLER ==========
    // Handles leave request form submission
    form.addEventListener('submit', async function(e) {
        e.preventDefault();  // Prevent default form submission

        // Get form values
        const startDate = startDateInput.value;  // Start date
        const endDate = endDateInput.value;  // End date
        const comment = document.getElementById('comment').value;  // User reasoning
        const usePoints = !usePointsCheckbox.checked;  // Invert checkbox: checked=false, unchecked=true

        // Validate: end date must be after start date
        if (new Date(startDate) > new Date(endDate)) {
            showError('End date must be after start date');
            return;
        }

        // Prepare request payload
        const requestData = {
            startDate: new Date(startDate).toISOString(),  // Convert to ISO 8601 format
            endDate: new Date(endDate).toISOString(),  // Convert to ISO 8601 format
            comment: comment,  // User reasoning
            usePoints: usePoints  // Whether to deduct points
        };

        try {
            // POST request to /leaves endpoint with JWT token
            const response = await fetch('/leaves', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + getAuthToken()  // Include JWT token
                },
                body: JSON.stringify(requestData)  // Send request data as JSON
            });

            if (response.ok) {
                // Success: show success message, reset form
                showSuccess('Leave request submitted successfully');
                form.reset();  // Clear form fields
                pointsInfo.classList.remove('visible');  // Hide points display
            } else {
                // Error: display backend error message
                const error = await response.json();
                showError(error.message || 'Failed to submit leave request');
            }
        } catch (error) {
            // Handle network errors or exceptions
            showError('An error occurred while submitting the request');
            console.error(error);
        }
    });

    // ========== CANCEL BUTTON HANDLER ==========
    // Resets form and redirects to login page
    cancelButton.addEventListener('click', function() {
        form.reset();  // Clear form fields
        pointsInfo.classList.remove('visible');  // Hide points display
        window.location.href = '/login';  // Redirect to login
    });

    // ========== INITIALIZATION ==========
    // Load user data on page load
    loadUserData();
});
