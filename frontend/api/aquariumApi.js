import apiClient from './apiClient.js';
import drawChart from './waterLogApi.js';

document.addEventListener("DOMContentLoaded", async () => {
  try {
    // 어항 목록 가져오기
    const response = await apiClient.get("/aquariums");
    const aquariumList = response.data;
    const container = document.getElementById("list-container");

    // 테이블 생성
    const table = document.createElement("table");
    table.className = "min-w-full table-auto border border-gray-300";
    table.innerHTML = `
      <thead class="bg-gray-100 text-gray-700">
        <tr>
          <th class="px-4 py-2 border-b">수조 아이디</th>
          <th class="px-4 py-2 border-b">수조 이름</th>
        </tr>
      </thead>
      <tbody id="aquarium-table-body"></tbody>
    `;
    container.appendChild(table);

    const tbody = table.querySelector("#aquarium-table-body");

    aquariumList.forEach(aqua => {
      const row = document.createElement("tr");
      row.className = "hover:bg-gray-50 cursor-pointer";

      row.innerHTML = `
        <td class="px-4 py-2 border-b text-center font-medium text-blue-700">${aqua.id}</td>
        <td class="px-4 py-2 border-b text-center">${aqua.name}</td>
      `;

      row.addEventListener("click", async () => {
        localStorage.setItem("selectedAquaId", aqua.id);
        drawChart(Number(aqua.id));
        await loadStatus(Number(aqua.id));
      });

      tbody.appendChild(row);
    });

    // 페이지 로딩 시 저장된 aquaId로 초기화
    const storedAquaId = Number(localStorage.getItem("selectedAquaId"));
    if (storedAquaId) {
      drawChart(storedAquaId);
      await loadStatus(storedAquaId);
    }
  } catch (err) {
    console.error("어항 목록 또는 상태 정보 가져오기 실패:", err);
  }
});

// 상태 정보 불러오기 함수
async function loadStatus(aquaId) {
  try {
    const safe = document.getElementById("status-desc");
    const danger = document.getElementById("danger-alert");

    const response = await apiClient.get(`aquarium/${aquaId}/status`);
    const isSafe = (
      response.data.temperatureStatus === "정상" &&
      response.data.phStatus === "정상" &&
      response.data.turbidityStatus === "정상"
    );

    safe.classList.toggle("hidden", !isSafe);
    danger.classList.toggle("hidden", isSafe);

    if (!isSafe) {
      danger.innerHTML = `
        위험 상태입니다. 즉시 조치를 취하세요!<br>
        온도 상태: ${response.data.temperatureStatus}<br>
        pH 상태: ${response.data.phStatus}<br>
        탁도 상태: ${response.data.turbidityStatus}
      `;
    }
  } catch (error) {
    console.error("상태 정보 가져오기 실패:", error);
  }
}
