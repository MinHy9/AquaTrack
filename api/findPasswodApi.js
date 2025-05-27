import apiClient from "./apiClient";

document.getElementById('send-reset-link').addEventListener('click', async() => {
    const email = document.getElementById('email').value;
    try {
         const response = await apiClient.put("/auth/randomPassword",{
            email : email
         })        
         alert(response.data);
        localStorage.setItem("isTempPassword", "true");
    }catch (error) {
         alert("랜덤 비밀번호 실패: " + (error.response?.data?.message || error.message));   
    }
});

