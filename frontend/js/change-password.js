import { API_BASE } from './config.js';

document.getElementById('change-password-btn').addEventListener('click', () => {
    const pw1 = document.getElementById('new-password').value.trim();
    const pw2 = document.getElementById('confirm-password').value.trim();

    if (!pw1 || !pw2) {
        alert('모든 칸을 입력해주세요.');
        return;
    }
    if (pw1 !== pw2) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }
    if (pw1.length < 8) {
        alert('비밀번호는 8자 이상이어야 합니다.');
        return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
        alert('로그인이 필요합니다. 다시 로그인해주세요.');
        location.href = 'login.html';
        return;
    }

    fetch(`${API_BASE}/api/auth/newPassword`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ newPassword: pw1 })
    })
        .then(res => {
            if (!res.ok) throw new Error('비밀번호 변경 실패');
            return res.text();
        })
        .then(msg => {
            alert(msg || '비밀번호가 변경되었습니다.');
            location.href = 'login.html';
        })
        .catch(err => {
            alert('에러: ' + err.message);
        });
});