import { API_BASE } from './config.js';

let stompClient;

export function initDashboard() {
    const socket = new SockJS(`${API_BASE}/ws`);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        stompClient.subscribe('/topic/sensor', (message) => {
            const data = JSON.parse(message.body);
            updateSensorCards(data);
        });
    });
}

function updateSensorCards(data) {
    document.querySelector('.temp-card .value').textContent = data.temperature.toFixed(1);
    document.querySelector('.ph-card .value').textContent = data.ph.toFixed(1);
    document.querySelector('.turb-card .value').textContent = data.turbidity.toFixed(1);

    const statusCard = document.getElementById('status-card');
    const statusText = document.getElementById('status-text');
    const statusDesc = document.getElementById('status-desc');
    const alertText = document.getElementById('danger-alert');
    const statusBadge = document.getElementById('status-badge');

    if (data.status === 'danger') {
        statusCard.classList.remove('status-normal');
        statusCard.classList.add('status-danger');
        statusText.textContent = '위험 상태';
        statusBadge.textContent = '위험';
        statusBadge.classList.remove('bg-green-100', 'text-green-800');
        statusBadge.classList.add('bg-red-100', 'text-red-800');
        statusDesc.textContent = '하나 이상의 수질 지표가 위험 범위에 있습니다.';
        alertText.classList.remove('hidden');
    } else {
        statusCard.classList.remove('status-danger');
        statusCard.classList.add('status-normal');
        statusText.textContent = '정상 상태';
        statusBadge.textContent = '정상';
        statusBadge.classList.remove('bg-red-100', 'text-red-800');
        statusBadge.classList.add('bg-green-100', 'text-green-800');
        statusDesc.textContent = '모든 수질 지표가 정상 범위 내에 있습니다.';
        alertText.classList.add('hidden');
    }
}
