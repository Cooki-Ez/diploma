document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginForm');

    setupLogoutButton();

    if (loginForm) {
        handleLoginForm();
    }

    function setupLogoutButton() {
        const logoutButton = document.querySelector('a[href="/auth/logout"]');
        if (logoutButton) {
            logoutButton.addEventListener('click', function (e) {
                e.preventDefault();
                localStorage.removeItem('jwt-token');
                window.location.href = '/login';
            });
        }
    }
});

function handleLoginForm() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('error-message');

    loginForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (response.ok && data['jwt-token']) {
                localStorage.setItem('jwt-token', data['jwt-token']);
                window.location.href = '/leaves-view';
            } else {
                showError(data.message || 'Login failed');
            }
        } catch (error) {
            showError('An error occurred during login');
        }
    });

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.add('visible');
        setTimeout(() => errorMessage.classList.remove('visible'), 5000);
    }
}
