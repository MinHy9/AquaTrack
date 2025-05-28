import apiClient from './apiClient.js'

document.addEventListener("DOMContentLoaded", () => {
  const feedSelect = document.getElementById("feed-select");
  const customization = document.getElementById("custom-times");

  feedSelect.addEventListener("change", () => {
    const selected = feedSelect.value;
    
    if (selected === "맞춤 설정") {
      customization.classList.remove("hidden");
    } else {
      customization.classList.add("hidden");
    }
  });
});

const saveBtn = document.getElementById("save-config-btn");

saveBtn.addEventListener('click', async () => {
    const aquariumId = Number(localStorage.getItem("selectedAquaId"));
    const fishType = document.getElementById("fish_type").value;
    const minTemperature = document.getElementById("cfg-temp-min").value;
    const maxTemperature = document.getElementById("cfg-temp-max").value;
    const minPH = document.getElementById("cfg-ph-min").value;
    const maxPH = document.getElementById("cfg-ph-max").value;
    const maxTurbidity = document.getElementById("cfg-turb-max").value;
    const feedingOption = feedSelect.value;

    if (!minTemperature || !maxTemperature || !minPH || !maxPH || !maxTurbidity || !aquariumId || aquariumId <= 0) {
        alert("입력하지 않은 값이 있습니다. 입력해주세요");
        return;
    }

    let feedingTime = [];
    if (feedingOption === "맞춤 설정") {
        const customInput = document.querySelector("#custom-times input").value;
        feedingTime = customInput
            ? customInput.split(',').map(time => time.trim())
            : [];

        if (feedingTime.length === 0 || feedingTime.some(t => !t)) {
            alert("맞춤 시간 입력이 올바르지 않습니다.");
            return;
        }
    } else {
        feedingTime = feedingOption.match(/\((.*?)\)/)?.[1]
            .split(',')
            .map(time => time.trim());
    }

    try {
        await apiClient.put(`/aquariums/${aquariumId}/thresholds`, {
            minTemperature: parseFloat(minTemperature),
            maxTemperature: parseFloat(maxTemperature),
            minPH: parseFloat(minPH),
            maxPH: parseFloat(maxPH),
            maxTurbidity: parseFloat(maxTurbidity)
        });

        await apiClient.post("/feeding/schedule", {
            feedingOption: feedingTime,
        });

        await apiClient.put(`/aquariums/${aquariumId}/fish`, {
            fishType,
        });

        alert("설정이 성공적으로 저장되었습니다.");
    } catch (error) {
        alert("저장에 실패했습니다: " + (error.response?.data?.message || error.message));
    }
});