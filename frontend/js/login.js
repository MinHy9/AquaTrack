import { API_BASE } from './config.js';

const passwordToggle = document.querySelector('.password-toggle');
const passwordInput = document.getElementById('password');
const passwordToggleIcon = document.getElementById('password-toggle-icon');
const loginError = document.getElementById('login-error');

passwordToggle.addEventListener('click', function () {
    const isPassword = passwordInput.type === 'password';
    passwordInput.type = isPassword ? 'text' : 'password';
    passwordToggleIcon.classList.toggle('ri-eye-line', !isPassword);
    passwordToggleIcon.classList.toggle('ri-eye-off-line', isPassword);
});

const loginButton = document.getElementById('login-button');
loginButton.addEventListener('click', function () {
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const rememberChecked = document.getElementById('remember').checked;

    loginError.classList.add('hidden');

    if (!email || !password) {
        loginError.classList.remove('hidden');
        return;
    }

    fetch(`${API_BASE}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    })
        .then(res => {
            if (!res.ok) throw new Error('로그인 실패');
            return res.json();
        })
        .then(data => {
            if (rememberChecked) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('loggedIn', 'true');
            } else {
                sessionStorage.setItem('token', data.token);
                sessionStorage.setItem('loggedIn', 'true');
            }

            alert('로그인 성공');
            location.href = 'index.html';
        })

        .catch(err => {
            loginError.textContent = err.message;
            loginError.classList.remove('hidden');
        });
});