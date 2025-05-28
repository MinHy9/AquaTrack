import apiClient from './apiClient.js'
const manualFeedingButton = document.getElementById("manual_feeding");
manualFeedingButton.addEventListener("click",async()=>{
    try {
        const aquariumId = Number(localStorage.getItem("selectedAquaId"));
        const response = await apiClient.post(`/feeding/manual/${aquariumId}`);
        alert(response.data);
    } catch (error) {
        const message = error.response?.data?.message || "";
        alert(message);
    }
});

function formatTime(date) {
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${month}월 ${day}일 ${hours}:${minutes}`;
}

document.addEventListener("DOMContentLoaded",async()=>{
     
    try {
        const aquariumId = Number(localStorage.getItem("selectedAquaId"));
        const lastFeeding = document.getElementById("last_feeding");
        const nextFeeding = document.getElementById("next_feeding");

        const response = await apiClient.get(`/feeding/schedule/${aquariumId}`);
        const schedulingDate = response.data;

        if (schedulingDate.last) {
            lastFeeding.textContent = formatTime(new Date(schedulingDate.last));
        } else {
            lastFeeding.textContent = "-";
        }

        if (schedulingDate.next) {
            nextFeeding.textContent = formatTime(new Date(schedulingDate.next));
        } else {
            nextFeeding.textContent = "-";
        }

    } catch (error) {
        const message = error.response?.data?.message || "";
        if (message.includes("어항이 없습니다")) {
            alert(message);
        } else {
            alert("급식 주기를 가져올 수 없습니다.");
        }
    }
})


