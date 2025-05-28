import apiClient from "./apiClient"; 
document.getElementById('register-btn').addEventListener('click', async() => {
    const name  = document.getElementById('aq-name').value.trim();
    const selectedFish = document.getElementById("fish_type").value;
    if (!name || !owner) {
      alert('모든 필드를 입력해주세요.');
      return;
    }
    try {
        const response = await apiClient.post('/aquariums',{
             name : name,
             fishName : selectedFish
    })
     alert(response.data);   
    } catch (error) {
        const message = error.response?.data?.message || "";
        alert(message);
    }    
});