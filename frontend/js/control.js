import { API_BASE, AUTH_HEADER } from './config.js';

export function bindControlButtons() {
    document.getElementById('feeder-btn')?.addEventListener('click', () => {
        controlDevice('feeder', 'ON');
    });

    document.getElementById('cooler-on-btn')?.addEventListener('click', () => {
        controlDevice('cooler', 'ON');
    });

    document.getElementById('cooler-off-btn')?.addEventListener('click', () => {
        controlDevice('cooler', 'OFF');
    });

    document.getElementById('pump-on-btn')?.addEventListener('click', () => {
        controlDevice('pump', 'ON');
    });

    document.getElementById('pump-off-btn')?.addEventListener('click', () => {
        controlDevice('pump', 'OFF');
    });
}

function controlDevice(device, command) {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) {
        alert('어항이 선택되지 않았습니다.');
        return;
    }

    let url;
    let options = {
        method: 'POST',
        headers: {
            ...AUTH_HEADER
        }
    };

    if (device === 'feeder') {
        // 수동 급여 엔드포인트 사용 (백엔드와 일치)
        url = `${API_BASE}/api/feeding/manual/${aquariumId}`;
        // 백엔드 엔드포인트는 요청 본문이 필요 없으므로 제거
        // Content-Type 헤더도 필요 없으므로 제거
    } else {
        // 다른 장치 제어 엔드포인트 사용 (기존 방식 유지)
        url = `${API_BASE}/api/control/${device}`;
        options.headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify({ aquariumId, activate: command === 'ON' });
    }

    fetch(url, options)
        .then(res => {
            if (!res.ok) throw new Error('제어 명령 실패');
            return res.text();
        })
        .then(msg => alert(msg))
        .catch(err => alert(err.message));
}
