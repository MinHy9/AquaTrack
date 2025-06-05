import {initCharts} from "./chart.js";
import {initDashboard} from "./dashboard.js";
import {bindControlButtons} from "./control.js";
import {initFeedingSettings} from "./feeding.js";
import {initThresholdSettings} from "./threshold.js";
import { renderNavbar } from './nav.js';
import { loadChartData, loadSingleChartData } from './chart.js';
import { API_BASE, AUTH_HEADER } from './config.js';

renderNavbar();
console.log("✅ index.js 로딩됨");

async function updateFishSelect() {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
        renderNavbar(true);
    } else {
        renderNavbar(false);
    }

    try {
        const res = await fetch(`${API_BASE}/api/aquariums/${aquariumId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!res.ok) throw new Error('어항 정보를 불러오지 못했습니다');

        const data = await res.json();
    } catch (err) {
        console.error(err);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const isLoggedIn = localStorage.getItem('loggedIn') === 'true' ||
        sessionStorage.getItem('loggedIn') === 'true';
    const guestMessage = document.getElementById('guest-message');
    const listContainer = document.getElementById('list-container');


    if (!isLoggedIn) {
        location.href = 'login.html';
        guestMessage.classList.remove('hidden');
        listContainer.classList.add('hidden');
        return;
    }
    else {
        guestMessage.classList.add('hidden');
        listContainer.classList.remove('hidden');
    }
    // initCharts(); // 차트 초기화만 우선 - 중복 호출 제거
    initDashboard(); // 실시간 센서 수치 표시용
    bindControlButtons();//제버튼 클릭
    initFeedingSettings();//먹이 공급관련
    initThresholdSettings();//기준값 설정

    // 어항 목록 로딩
    (async function loadList() {
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        console.log("🐟 loadList 실행됨, token:", token); // ✅ 추가
        try {
            const res = await fetch(`${API_BASE}/api/aquariums`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log("📡 응답 상태코드:", res.status); // ✅ 추가
            const list = await res.json();
            console.log("📦 어항 목록 데이터:", list); // ✅ 추가
            const container = document.getElementById('list-container');
            if (list.length === 0) {
                container.innerHTML = '<p class="text-gray-600">등록된 어항이 없습니다.</p>';
                return;
            }
            container.innerHTML = list.map(aq => `
              <div class="p-4 border rounded hover:shadow aquarium-card" data-id="${aq.aquariumId}" data-boardid="${aq.boardId}">
                <h3 class="text-lg font-semibold">${aq.name}</h3>
                <p class="text-gray-700">어종: ${aq.fishName}</p>
                <p class="text-gray-600">보드 ID: ${aq.boardId}</p>
                <p class="text-sm text-gray-500">ID: ${aq.aquariumId}</p>
                <button class="delete-btn mt-2 text-sm text-red-600 hover:underline" data-id="${aq.aquariumId}">
                삭제
                </button>
              </div>
            `).join('');
            // ✅ 목록이 렌더링된 후에 첫 번째 어항을 선택 상태로 저장
            const firstAquarium = list[0];
            localStorage.setItem('selectedAquariumId', firstAquarium.aquariumId);
            localStorage.setItem('selectedAquariumBoardId', firstAquarium.boardId); // 보드 ID 저장
            updateFishSelect(); // 이 시점에 호출해야 정상 작동
            initCharts(); // 어항 선택 후 차트 초기화 및 데이터 로딩
        } catch (e) {
            document.getElementById('list-container').innerHTML =
                `<p class="text-red-500">목록 불러오기 실패: ${e.message}</p>`;
        }
    })();
    // 카드 클릭 시 선택된 어항 ID 및 보드 ID 저장
    document.addEventListener('click', e => {
        const card = e.target.closest('.aquarium-card');
        if (!card) return;

        const id = card.dataset.id;
        const boardId = card.dataset.boardid; // 보드 ID 가져오기
        localStorage.setItem('selectedAquariumId', id);
        localStorage.setItem('selectedAquariumBoardId', boardId); // 보드 ID 저장
        console.log(`어항 ${id} 선택됨 (보드 ID: ${boardId})`);

        // 시각적 강조 (선택된 카드)
        document.querySelectorAll('.aquarium-card').forEach(c => c.classList.remove('ring-2', 'ring-blue-500'));
        card.classList.add('ring-2', 'ring-blue-500');

        // 환경 설정 값 업데이트
        fetch(`${API_BASE}/api/aquariums/${id}/thresholds`, {
            headers: AUTH_HEADER
        })
        .then(res => res.json())
        .then(data => {
            document.getElementById('min-temp').value = data.minTemperature ?? 24.0;
            document.getElementById('max-temp').value = data.maxTemperature ?? 27.0;
            document.getElementById('min-ph').value = data.minPH ?? 6.5;
            document.getElementById('max-ph').value = data.maxPH ?? 8.0;
            document.getElementById('min-turb').value = data.minTurbidity ?? 0.0;
            document.getElementById('max-turb').value = data.maxTurbidity ?? 11.0;
        })
        .catch(error => {
            console.error('환경 설정 값 로드 중 오류 발생:', error);
        });

        // 차트 업데이트
        const activeRangeButton = document.querySelector('.multi-chart-range-btn.bg-blue-100');
        const range = activeRangeButton ? activeRangeButton.dataset.range : 'daily';
        loadChartData(range);

        const activeMetricButton = document.querySelector('.metric-btn[data-metric].bg-blue-100');
        if (activeMetricButton) {
            const metric = activeMetricButton.dataset.metric;
            loadSingleChartData(metric, range);
        }

        // ✅ 추가: .metric-btn 클릭 시 차트 업데이트
        document.querySelectorAll('.metric-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                document.querySelectorAll('.metric-btn').forEach(b => {
                    b.classList.remove('bg-blue-100', 'text-blue-700');
                    b.classList.add('bg-gray-100', 'text-gray-700');
                });
                btn.classList.remove('bg-gray-100', 'text-gray-700');
                btn.classList.add('bg-blue-100', 'text-blue-700');
            });
        });

        // ✅ 추가: metric 버튼 클릭 시 singleChart 업데이트
        document.querySelectorAll('.metric-btn[data-metric]').forEach(btn => {
            btn.addEventListener('click', () => {
                const metric = btn.dataset.metric;
                const activeRangeButton = document.querySelector('.metric-btn[data-range].bg-blue-100');
                const range = activeRangeButton ? activeRangeButton.dataset.range : 'daily';
                loadSingleChartData(metric, range);
            });
        });
    });

    //자동어종표시
    //updateFishSelect(); // 자동 어종 표시
    window.logout = function () {
        localStorage.removeItem('loggedIn');
        localStorage.removeItem('token');
        sessionStorage.removeItem('loggedIn');
        sessionStorage.removeItem('token');
        alert("로그아웃 되었습니다.");
        location.href = 'login.html';
    };

    document.querySelectorAll('.metric-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.metric-btn').forEach(b => {
                b.classList.remove('bg-blue-100', 'text-blue-700');
                b.classList.add('bg-gray-100', 'text-gray-700');
            });
            btn.classList.remove('bg-gray-100', 'text-gray-700');
            btn.classList.add('bg-blue-100', 'text-blue-700');
        });
    });

    // 삭제 버튼 클릭 시 어항 삭제
    document.addEventListener("click", async function (event) {
        if (event.target.classList.contains("delete-btn")) {
            const aquariumId = event.target.getAttribute("data-id");
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');

            if (confirm("정말 이 어항을 삭제하시겠습니까?")) {
                try {
                    const res = await fetch(`${API_BASE}/api/aquariums/${aquariumId}`, {
                        method: "DELETE",
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });

                    if (res.ok) {
                        alert("어항이 삭제되었습니다.");
                        // 1. DOM에서 어항 카드 제거
                        const card = document.querySelector(`.aquarium-card[data-id="${aquariumId}"]`);
                        if (card) card.remove();

                        // 2. 선택된 어항이 삭제된 경우, 새로 선택할 어항 지정
                        const currentSelected = localStorage.getItem('selectedAquariumId');
                        if (currentSelected === aquariumId) {
                            const remainingCards = document.querySelectorAll('.aquarium-card');
                            if (remainingCards.length > 0) {
                                const newId = remainingCards[0].dataset.id;
                                localStorage.setItem('selectedAquariumId', newId);
                                console.log("🔁 새로운 어항 선택됨:", newId);
                            } else {
                                localStorage.removeItem('selectedAquariumId');
                                console.log("🧼 모든 어항이 삭제됨");
                            }
                        }
                        // 어항 삭제 후 차트 및 데이터 새로고침
                        const remainingCards = document.querySelectorAll('.aquarium-card');
                        if (remainingCards.length > 0) {
                             const newId = remainingCards[0].dataset.id;
                             localStorage.setItem('selectedAquariumId', newId);
                             updateFishSelect();
                             initCharts(); // 새 어항 선택 후 차트 새로고침
                         } else {
                             localStorage.removeItem('selectedAquariumId');
                             console.log("🧼 모든 어항이 삭제됨");
                             // 모든 어항 삭제 시 차트 초기화 또는 숨김 처리 필요
                             // 현재는 페이지 새로고침으로 처리될 것으로 예상
                         }
                    } else {
                        const msg = await res.text();
                        alert(`삭제 실패: ${msg}`);
                    }
                } catch (err) {
                    alert("에러 발생: " + err.message);
                }
            }
        }
    });
});

