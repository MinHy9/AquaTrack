import { API_BASE, AUTH_HEADER } from './config.js';

export function initFeedingSettings() {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    const AUTH_HEADER = {
        'Authorization': `Bearer ${token}`
    };
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
                const customInput = document.getElementById('custom-feed-times');
                let times = [];
                if (type === 'custom') {
                    if (!customInput || !customInput.value) {
                        alert('맞춤 시간을 입력해주세요.');
                        return;
                    }
                    times = customInput.value.split(',').map(t => t.trim()).filter(t => t !== '');
                }

                fetch(`${API_BASE}/api/feeding/schedule`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ aquariumId, scheduleType: type, times })
                })
                    .then(res => {
                        if (!res.ok) throw new Error('저장 실패');
                        return res.text();
                    })
                    .then(() => {
                        // 저장된 시간 표시를 업데이트
                        const display = document.getElementById('current-schedule');
                        const lastFeed = document.getElementById('last-feed-time');
                        const nextFeed = document.getElementById('next-feed-time');

                        if (display) {
                            if (type === 'custom' && times) {
                                display.innerText = `현재 저장된 급식 시간: ${times.join(', ')}`;
                            } else {
                                display.innerText = `현재 저장된 급식 시간: ${type}`;
                            }
                        }
                        if (lastFeed && nextFeed) {
                            if (type === 'custom' && times?.length > 0) {
                                const now = new Date();
                                const sortedTimes = times.map(t => t.trim()).sort();
                                const last = sortedTimes[0];
                                const next = sortedTimes[1] || sortedTimes[0];

                                lastFeed.innerText = `오늘 ${last}`;
                                nextFeed.innerText = `오늘 ${next}`;
                            } else if (type) {
                                const timeList = type.split(',').map(t => t.trim()).sort();
                                lastFeed.innerText = `오늘 ${timeList[0]}`;
                                nextFeed.innerText = `오늘 ${timeList[1] || timeList[0]}`;
                            }
                        }
                        alert("급식 시간이 저장되었습니다!");
                    })
                    .catch(err => alert(err.message));
            });
        });
}
