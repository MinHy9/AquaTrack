import { API_BASE, AUTH_HEADER } from './config.js';

export function initThresholdSettings() {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

    // 기준값 조회
    fetch(`${API_BASE}/api/aquarium/config?aquariumId=${aquariumId}`, {
        headers: AUTH_HEADER
    })
        .then(res => res.json())
        .then(data => {
            document.getElementById('min-temp').value = data.minTemperature ?? '';
            document.getElementById('max-temp').value = data.maxTemperature ?? '';
            document.getElementById('min-ph').value = data.minPh ?? '';
            document.getElementById('max-ph').value = data.maxPh ?? '';
            document.getElementById('min-turb').value = data.minTurbidity ?? '';
            document.getElementById('max-turb').value = data.maxTurbidity ?? '';
        });

    // 저장 버튼
    document.getElementById('save-config-btn')?.addEventListener('click', () => {
        const body = {
            aquariumId,
            minTemperature: parseFloat(document.getElementById('min-temp').value),
            maxTemperature: parseFloat(document.getElementById('max-temp').value),
            minPh: parseFloat(document.getElementById('min-ph').value),
            maxPh: parseFloat(document.getElementById('max-ph').value),
            minTurbidity: parseFloat(document.getElementById('min-turb').value),
            maxTurbidity: parseFloat(document.getElementById('max-turb').value),
        };

        fetch(`${API_BASE}/api/aquarium/config`, {
            method: 'POST',
            headers: {
                ...AUTH_HEADER,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        })
            .then(res => {
                if (!res.ok) throw new Error('저장 실패');
                return res.text();
            })
            .then(alert)
            .catch(err => alert(err.message));
    });
}
