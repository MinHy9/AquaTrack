import { API_BASE } from './config.js';

const passwordToggle = document.querySelector('.password-toggle');
const passwordInput = document.getElementById('password');
const passwordToggleIcon = document.getElementById('password-toggle-icon');

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

    if (!email || !password) {
        alert('이메일과 비밀번호를 모두 입력해주세요.');
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
            localStorage.setItem('token', data.token);
            localStorage.setItem('loggedIn', 'true');
            alert('로그인 성공');
            location.href = 'index.html';
        })
        .catch(err => {
            alert(err.message);
        });
});