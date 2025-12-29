// Evaluate Leave Request Page JavaScript
// Handles leave request evaluation, approval, and rejection by managers/admins

// DOM Elements
document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);  // Parse URL parameters
    const leaveRequestId = urlParams.get('id');  // Extract leave request ID from URL (e.g., ?id=5)

    const approveButton = document.getElementById('approveButton');  // Approve button (green)
    const rejectButton = document.getElementById('rejectButton');  // Reject button (red)
    const evaluationComment = document.getElementById('evaluationComment');  // Comment textarea
    const errorMessage = document.getElementById('error-message');  // Error message display
    const successMessage = document.getElementById('success-message');  // Success message display

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

    // ========== HELPER: FORMAT DATE ==========
    // Formats ISO date string to readable format (dd/mm/yyyy)
    function formatDate(dateString) {
        const date = new Date(dateString);  // Parse ISO date string
        return date.toLocaleDateString('en-GB', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    // ========== HELPER: CALCULATE DAYS ==========
    // Calculates number of days between start and end date
    // Logic: (end - start) in milliseconds / milliseconds per day + 1 (inclusive)
    function calculateDays(startDate, endDate) {
        const start = new Date(startDate);  // Parse start date
        const end = new Date(endDate);  // Parse end date
        const diffTime = end - start;  // Difference in milliseconds
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;  // Convert to days
        return diffDays > 0 ? diffDays : 0;  // Return 0 if negative
    }

    // ========== METHOD: LOAD LEAVE REQUEST DETAILS ==========
    // Retrieves leave request details from backend for evaluation
    // Fetches data for: dates, projects, reasoning
    async function loadLeaveRequestDetails() {
        // Validate: leave request ID is required
        if (!leaveRequestId) {
            showError('Leave request ID is required');
            return;
        }

        try {
            // GET request to /leaves/{id} endpoint with JWT token
            const response = await fetch(`/leaves/${leaveRequestId}`, {
                headers: {
                    'Authorization': 'Bearer ' + getAuthToken()  // Include JWT token
                }
            });

            if (response.ok) {
                const leaveRequest = await response.json();  // Parse response as JSON

                // Extract date information
                const startDate = leaveRequest.startDate;
                const endDate = leaveRequest.endDate;
                const days = calculateDays(startDate, endDate);  // Calculate days

                // Display formatted dates with day counts in brackets
                document.getElementById('detailStartDate').textContent = formatDate(startDate);
                document.getElementById('startDays').textContent = `(${days} day${days > 1 ? 's' : ''})`;
                document.getElementById('detailEndDate').textContent = formatDate(endDate);
                document.getElementById('endDays').textContent = `(${days} day${days > 1 ? 's' : ''})`;

                // Display user's reasoning for leave request
                document.getElementById('detailReasoning').textContent =
                    leaveRequest.comment || 'No reasoning provided';

                // Display employee's assigned projects
                // This shows which projects the employee is working on
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

    // ========== METHOD: EVALUATE LEAVE REQUEST ==========
    // Updates leave request status (APPROVED or DECLINED) with comment
    // Called when approve or reject button is clicked
    async function evaluateLeaveRequest(approved) {
        const comment = evaluationComment.value.trim();  // Get user's comment (or default)

        // Prepare request payload
        const requestData = {
            status: approved ? 'APPROVED' : 'DECLINED',  // Set status based on button
            comment: comment || (approved ? 'Approved!' : 'Rejected!')  // Use default message if empty
        };

        try {
            // PATCH request to /leaves/{id} endpoint with JWT token
            const response = await fetch(`/leaves/${leaveRequestId}`, {
                method: 'PATCH',  // Use PATCH for partial update
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + getAuthToken()  // Include JWT token
                },
                body: JSON.stringify(requestData)  // Send update data as JSON
            });

            if (response.ok) {
                // Success: show success message
                showSuccess(approved ? 'Leave request approved!' : 'Leave request rejected!');
                // Close window after 2 seconds (typically used in popup/modal context)
                setTimeout(() => {
                    window.close();
                }, 2000);
            } else {
                // Error: display backend error message
                const error = await response.json();
                showError(error.message || 'Failed to evaluate leave request');
            }
        } catch (error) {
            // Handle network errors or exceptions
            showError('An error occurred while evaluating the request');
            console.error(error);
        }
    }

    // ========== APPROVE BUTTON HANDLER ==========
    // Approves the leave request
    // Pre-fills comment with "Approved!" and calls evaluateLeaveRequest(true)
    approveButton.addEventListener('click', function() {
        evaluationComment.value = 'Approved!';  // Set default approve message
        evaluateLeaveRequest(true);  // Call evaluation with approved=true
    });

    // ========== REJECT BUTTON HANDLER ==========
    // Rejects the leave request
    // Pre-fills comment with "Rejected!" and calls evaluateLeaveRequest(false)
    rejectButton.addEventListener('click', function() {
        evaluationComment.value = 'Rejected!';  // Set default reject message
        evaluateLeaveRequest(false);  // Call evaluation with approved=false
    });

    // ========== INITIALIZATION ==========
    // Load leave request details if ID is provided in URL
    // Page is accessed via: /evaluate-leave-request?id={leaveRequestId}
    if (leaveRequestId) {
        loadLeaveRequestDetails();  // Load and display request details
    } else {
        showError('Please provide a leave request ID in the URL');
    }
});
