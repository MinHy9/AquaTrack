import { API_BASE, AUTH_HEADER } from './config.js';


document.getElementById('register-btn').addEventListener('click', () => {
    const name  = document.getElementById('aq-name').value.trim();
    const fishName = document.getElementById('aq-fishName').value.trim();
    if (!name || !fishName) {
        alert('모든 필드를 입력해주세요.');
        return;
    }

    fetch(`${API_BASE}/api/aquariums`, {
        method: 'POST',
        headers: {
            ...AUTH_HEADER,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, fishName })
    })
        .then(res => {
            if (!res.ok) throw new Error('어항 등록 실패');
            return res.json();
        })
        .then(json => {
            alert(`등록 완료! 어항 ID: ${json.aquariumId}`);
            location.href = 'index.html';
        })
        .catch(err => {
            alert('등록 실패: ' + err.message);
        });
});