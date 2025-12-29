// Login Page JavaScript
// Handles user authentication and JWT token storage

// DOM Elements
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');  // Login form element
    const errorMessage = document.getElementById('error-message');  // Error message display div

    // ========== FORM SUBMIT HANDLER ==========
    // Handles the login form submission
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();  // Prevent default form submission

        // Get form input values
        const username = document.getElementById('username').value;  // User email
        const password = document.getElementById('password').value;  // User password

        try {
            // Send POST request to backend login endpoint
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'  // Specify JSON content type
                },
                body: JSON.stringify({ username, password })  // Send credentials as JSON
            });

            // Parse response from backend
            const data = await response.json();

            // Check if login was successful
            if (response.ok && data['jwt-token']) {
                // STORE JWT TOKEN: Save the token to localStorage for subsequent API calls
                // This is the AUTHORIZATION CHANGE that simplifies the app
                localStorage.setItem('jwt-token', data['jwt-token']);

                // Redirect user to create-leave-request page after successful login
                window.location.href = '/create-leave-request';
            } else {
                // Show error message from backend or generic login failure message
                showError(data.message || 'Login failed');
            }
        } catch (error) {
            // Handle network errors or other exceptions
            showError('An error occurred during login');
            console.error(error);
        }
    });

    // ========== ERROR DISPLAY HELPER ==========
    // Displays error message to user and auto-hides after 5 seconds
    function showError(message) {
        errorMessage.textContent = message;  // Set error message text
        errorMessage.classList.add('visible');  // Make error message visible
        setTimeout(() => {
            errorMessage.classList.remove('visible');  // Hide after 5 seconds
        }, 5000);
    }
});
