import { API_BASE } from './config.js';

document.getElementById('send-reset-link').addEventListener('click', () => {
    const email = document.getElementById('email').value.trim();

    if (!email) {
        alert("이메일을 입력해주세요.");
        return;
    }
    if (!email.includes('@')) {
        alert('이메일 형식을 확인해주세요.');
        return;
    }

    fetch(`${API_BASE}/api/auth/resetPassword`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
    })
        .then(res => {
            if (!res.ok) throw new Error('비밀번호 재설정 실패');
            return res.text();
        })
        .then(msg => {
            alert(msg || '임시 비밀번호를 이메일로 전송했습니다.');
            location.href = 'login.html';
        })
        .catch(err => {
            alert('에러: ' + err.message);
        });
});