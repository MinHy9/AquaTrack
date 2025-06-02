import { API_BASE, AUTH_HEADER } from './config.js';

export function initThresholdSettings() {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

    // 기준값 조회
    fetch(`${API_BASE}/api/aquariums/${aquariumId}/thresholds`, {
        headers: AUTH_HEADER
    })
        .then(res => res.json())
        .then(data => {
            document.getElementById('min-temp').value = data.minTemperature ?? 24.0;
            document.getElementById('max-temp').value = data.maxTemperature ?? 27.0;
            document.getElementById('min-ph').value = data.minPh ?? 6.5;
            document.getElementById('max-ph').value = data.maxPh ?? 8.0;
            document.getElementById('min-turb').value = data.minTurbidity ?? 0.0;
            document.getElementById('max-turb').value = data.maxTurbidity ?? 11.0;
            console.log("🎯 서버로부터 받은 maxTurbidity:", data.maxTurbidity);
        });

    // 저장 버튼
    document.getElementById('save-config-btn')?.addEventListener('click', () => {
        const button = document.getElementById('save-config-btn');
        const successMsg = document.getElementById('save-success-msg');
        const minTempVal = document.getElementById('min-temp').value;
        const maxTempVal = document.getElementById('max-temp').value;
        const minPhVal = document.getElementById('min-ph').value;
        const maxPhVal = document.getElementById('max-ph').value;
        const minTurbVal = document.getElementById('min-turb')?.value ?? 0.0;
        const maxTurbVal = document.getElementById('max-turb').value;


        const body = {
            aquariumId,
            minTemperature: minTempVal ? parseFloat(minTempVal) : 24.0,
            maxTemperature: maxTempVal ? parseFloat(maxTempVal) : 27.0,
            minPh: minPhVal ? parseFloat(minPhVal) : 6.5,
            maxPh: maxPhVal ? parseFloat(maxPhVal) : 8.0,
            minTurbidity: parseFloat(minTurbVal),
            maxTurbidity: maxTurbVal ? parseFloat(maxTurbVal) : 11.0,

        };
        if (body.minTemperature >= body.maxTemperature) {
            alert("최소 수온은 최대 수온보다 작아야 합니다.");
            return;
        }
        if (body.minPh >= body.maxPh) {
            alert("최소 pH는 최대 pH보다 작아야 합니다.");
            return;
        }
        // 2번: 버튼 로딩 처리
        button.disabled = true;
        const originalText = button.textContent;
        button.textContent = '저장 중...';

        fetch(`${API_BASE}/api/aquariums/${aquariumId}/thresholds`, {
            method: 'PUT',
            headers: {
                ...AUTH_HEADER,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        })
            .then(async res => {
                if (!res.ok) {
                    const errorText = await res.text();
                    console.error("❌ 저장 실패 응답 내용:", errorText);
                    throw new Error('저장 실패: ' + errorText);
                }
                return res.text();
            })
            .then(() => {
                if (successMsg) {
                    successMsg.classList.remove('hidden');
                    setTimeout(() => successMsg.classList.add('hidden'), 3000);
                }
                initThresholdSettings();
            })
            .catch(err => alert(err.message))
            .finally(() => {
                button.disabled = false;
                button.textContent = originalText;
            });
    });
}
