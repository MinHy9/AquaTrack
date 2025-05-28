import apiClient from './apiClient.js'
const pumpOnButton = document.getElementById("water_pan_on");
const coolOnButton = document.getElementById("cooling_pan_on");
const pupmOffButton = document.getElementById("water_pan_off");
const coolOfButton = document.getElementById("cooling_pan_off");
const aquaId = localStorage.getItem("selectedAquaId");

function getAquariumId() {
    return Number(aquaId);
}

pupmOffButton.addEventListener("click",async()=>{
    try{
        const response = await apiClient.post('/control/pump',{
            aquariumId: getAquariumId(),
            activate: false
        });
        alert(response.data);
    }catch(error){
       alert(error.response?.data?.message || "제어 요청 실패");
    }
});


pumpOnButton.addEventListener("click",async()=>{
    try{
        const response = await apiClient.post('/control/pump',{
            aquariumId: getAquariumId(),
            activate: true
        });
        alert(response.data);
    }catch(error){
       alert(error.response?.data?.message || "제어 요청 실패");
    }
});

coolOnButton.addEventListener("click",async() =>{
    try{
        const response = await apiClient.post('/control/cooler',{
            aquariumId: getAquariumId(),
            activate: true
        })
        alert(response.data);
    }catch(error){
        alert(error.response?.data?.message || "제어 요청 실패");
    }
});

coolOfButton.addEventListener("click",async() =>{
    try{
        const response = await apiClient.post('/control/cooler',{
            aquariumId: getAquariumId(),
            activate: false
        })
        alert(response.data);
    }catch(error){
        alert(error.response?.data?.message || "제어 요청 실패");
    }
})
