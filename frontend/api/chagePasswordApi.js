import apiClient from './apiClient.js'
document.getElementById('change-password-btn').addEventListener('click', async() => {
        const pw1 = document.getElementById('new-password').value.trim();
        const pw2 = document.getElementById('confirm-password').value.trim();

        if (!pw1 || !pw2) {
            alert('모든 칸을 입력해주세요.');
            return;
        }
        if (pw1 !== pw2) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

       try{
         const response = await apiClient.put("/auth/newPassword",{
            password : pw2,
         })
            alert(response.data);
            location.href = 'index.html';
       }catch(error){
            alert(" 실패: " + (error.response?.data?.message || error.message));
       }

});