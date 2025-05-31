import { API_BASE, AUTH_HEADER } from './config.js';

export function initFeedingSettings() {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

    fetch(`${API_BASE}/api/feeding/schedule?aquariumId=${aquariumId}`, {
        headers: AUTH_HEADER
    })
        .then(res => res.json())
        .then(data => {
            const selectEl = document.getElementById('feed-select');
            const customWrapper = document.getElementById('custom-times');
            const customInput = document.getElementById('custom-input');

            // 저장된 스케줄 불러오기
            if (data.scheduleType === 'custom') {
                selectEl.value = 'custom';
                customWrapper.classList.remove('hidden');
                customInput.value = data.times.join(',');
            } else {
                selectEl.value = data.scheduleType;
                customWrapper.classList.add('hidden');
            }

            // select 변경 시 custom 입력창 보여주기
            selectEl.addEventListener('change', () => {
                if (selectEl.value === 'custom') {
                    customWrapper.classList.remove('hidden');
                } else {
                    customWrapper.classList.add('hidden');
                }
            });

            // 저장 버튼 클릭 시 POST
            document.getElementById('save-feed-btn')?.addEventListener('click', () => {
                const type = selectEl.value;
                const times = type === 'custom' ? customInput.value.split(',').map(t => t.trim()) : null;

                fetch(`${API_BASE}/api/feeding/schedule`, {
                    method: 'POST',
                    headers: {
                        ...AUTH_HEADER,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ aquariumId, scheduleType: type, times })
                })
                    .then(res => {
                        if (!res.ok) throw new Error('저장 실패');
                        return res.text();
                    })
                    .then(alert)
                    .catch(err => alert(err.message));
            });
        });
}
