import {initCharts} from "./chart.js";
import {initDashboard} from "./dashboard.js";
import {bindControlButtons} from "./control.js";
import {initFeedingSettings} from "./feeding.js";
import {initThresholdSettings} from "./threshold.js";
console.log("✅ index.js 로딩됨");
const API_BASE = location.origin.includes("localhost") ? "http://localhost:8080" : location.origin;

async function updateFishSelect() {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (!token) {
        console.warn("🚫 토큰 없음 - updateFishSelect 중단");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/aquariums/${aquariumId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!res.ok) throw new Error('어항 정보를 불러오지 못했습니다');

        const data = await res.json();
        document.getElementById('fish-select').value = data.fishName;
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
    initCharts(); // 차트 초기화만 우선
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
              <div class="p-4 border rounded hover:shadow aquarium-card" data-id="${aq.aquariumId}">
                <h3 class="text-lg font-semibold">${aq.name}</h3>
                <p class="text-gray-700">어종: ${aq.fishName}</p>
                <p class="text-sm text-gray-500">ID: ${aq.aquariumId}</p>
              </div>
            `).join('');
            // ✅ 목록이 렌더링된 후에 첫 번째 어항을 선택 상태로 저장
            const firstId = list[0].aquariumId;
            localStorage.setItem('selectedAquariumId', firstId);
            updateFishSelect(); // 이 시점에 호출해야 정상 작동
        } catch (e) {
            document.getElementById('list-container').innerHTML =
                `<p class="text-red-500">목록 불러오기 실패: ${e.message}</p>`;
        }
    })();
    // 카드 클릭 시 선택된 어항 ID 저장
    document.addEventListener('click', e => {
        const card = e.target.closest('.aquarium-card');
        if (!card) return;

        const id = card.dataset.id;
        localStorage.setItem('selectedAquariumId', id);
        console.log(`어항 ${id} 선택됨`);

        // 시각적 강조 (선택된 카드)
        document.querySelectorAll('.aquarium-card').forEach(c => c.classList.remove('ring-2', 'ring-blue-500'));
        card.classList.add('ring-2', 'ring-blue-500');
    });

    //자동어종표시
    //updateFishSelect(); // 자동 어종 표시

    //로그인상태
    const nav = document.getElementById('nav-links');

    if (isLoggedIn) {
        nav.innerHTML = `
        <a href="index.html" class="text-primary hover:text-primary/80">대시보드</a>
        <a href="aquarium-register.html" class="text-primary hover:text-primary/80">어항 등록</a>
        <a href="#" class="text-primary hover:text-primary/80" onclick="logout()">로그아웃</a>
      `;
    } else {
        nav.innerHTML = `
        <a href="login.html" class="text-primary hover:text-primary/80">로그인</a>
      `;
    }
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
});

