import { API_BASE } from './config.js';
import { updateChartsRealtime } from './chart.js';

let stompClient;
let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 5;

export function initDashboard() {
    connectWebSocket();
}

function connectWebSocket() {
    const socket = new SockJS(`${API_BASE}/ws`);
    stompClient = Stomp.over(socket);
    
    // 디버그 모드 활성화
    stompClient.debug = function(str) {
        console.log('STOMP: ' + str);
    };

    stompClient.connect({}, 
        // 성공 콜백
        () => {
            console.log('WebSocket 연결 성공');
            reconnectAttempts = 0;
            
            stompClient.subscribe('/topic/sensor', (message) => {
                try {
                    const data = JSON.parse(message.body);
                    console.log('수신된 센서 데이터:', data);
                    console.log('데이터 상태:', data.status);
                    console.log('온도:', data.temperature);
                    updateSensorCards(data);
                } catch (error) {
                    console.error('메시지 처리 중 오류:', error);
                }
            });
        },
        // 에러 콜백
        (error) => {
            console.error('WebSocket 연결 실패:', error);
            console.error('연결 실패 상세:', error.headers);
            console.error('연결 실패 메시지:', error.body);
            
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                reconnectAttempts++;
                console.log(`재연결 시도 ${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS}`);
                setTimeout(connectWebSocket, 5000); // 5초 후 재연결 시도
            } else {
                console.error('최대 재연결 시도 횟수 초과');
            }
        }
    );
}

function updateSensorCards(data) {
    document.querySelector('.bg-blue-100 .text-3xl').textContent = data.temperature.toFixed(1);
    document.querySelector('.bg-green-100 .text-3xl').textContent = data.ph.toFixed(1);
    document.querySelector('.bg-purple-100 .text-3xl').textContent = data.turbidity.toFixed(1);

    const statusCard = document.getElementById('status-card');
    const statusText = document.getElementById('status-text');
    const statusDesc = document.getElementById('status-desc');
    const alertText = document.getElementById('danger-alert');
    const statusBadge = document.getElementById('status-badge');

    if (data.status.toLowerCase() === 'danger') {
        statusCard.classList.remove('status-normal');
        statusCard.classList.add('status-danger');
        statusText.textContent = '위험 상태';
        statusText.classList.add('text-red-600');
        statusBadge.textContent = '위험';
        statusBadge.classList.remove('bg-green-100', 'text-green-800');
        statusBadge.classList.add('bg-red-100', 'text-red-800');
        statusDesc.textContent = '하나 이상의 수질 지표가 위험 범위에 있습니다.';
        statusDesc.classList.add('text-red-600');
        alertText.classList.remove('hidden');
    } else {
        statusCard.classList.remove('status-danger');
        statusCard.classList.add('status-normal');
        statusText.textContent = '정상 상태';
        statusText.classList.remove('text-red-600');
        statusBadge.textContent = '정상';
        statusBadge.classList.remove('bg-red-100', 'text-red-800');
        statusBadge.classList.add('bg-green-100', 'text-green-800');
        statusDesc.textContent = '모든 수질 지표가 정상 범위 내에 있습니다.';
        statusDesc.classList.remove('text-red-600');
        alertText.classList.add('hidden');
    }
}
