import apiClient from './apiClient.js'

document.addEventListener("DOMContentLoaded", async () => {
  try {
    const response = await apiClient.get("/aquariums");
    const aquariumList = response.data;

    const aqua_list = document.getElementById("list-container");

    aquariumList.forEach(aqua => {
      const card = document.createElement("div");
      card.className = "p-4 bg-white rounded shadow hover:shadow-lg transition";

      card.innerHTML = `
        <span class="text-lg font-semibold text-gray-800">어항아이디: ${aqua.id}</span>
        <p class="text-sm text-gray-500">어항이름: ${aqua.name}</p>
      `;

      aqua_list.appendChild(card);
    });
  } catch (err) {
    console.error("어항 목록 가져오기 실패:", err);
  }
});

document.addEventListener("DOMContentLoaded",async()=>{
  try{
    const safe = document.getElementById("status-desc");
    const danger = document.getElementById("danger-alert");
    const response = await apiClient.get('aquarium/status');
    const isSafe = (
      response.data.temperatureStatus === "정상" &&
      response.data.phStatus === "정상" &&
      response.data.turbidityStatus === "정상"
  );

    safe.classList.toggle("hidden", !isSafe);
    danger.classList.toggle("hidden", isSafe);
    
    if(!isSafe){
      danger.innerHTML = `위험 상태입니다. 즉시 조치를 취하세요! <br>온도 상태 : ${response.data.temperatureStatus}<br>
       ph 상태 : ${response.data.phStatus}, <br>탁도 상테 : ${response.data.turbidityStatus} `;
    }

  }catch(error){
    console.error("상테 정보 가져오기 실패");
  }
})


