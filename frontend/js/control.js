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

    fetch(`${API_BASE}/api/control/device`, {
        method: 'POST',
        headers: {
            ...AUTH_HEADER,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ aquariumId, device, command })
    })
        .then(res => {
            if (!res.ok) throw new Error('제어 명령 실패');
            return res.text();
        })
        .then(msg => alert(msg))
        .catch(err => alert(err.message));
}
