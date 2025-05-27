import apiClient from './apiClient.js'

apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem("accessToken");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;  
});

document.addEventListener("DOMContentLoaded", () => {
    const loginBtn = document.getElementById("login-button");

    loginBtn.addEventListener("click", async () => {
        const userId = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        if (!userId || !password) {
            alert("이메일과 비밀번호를 입력해주세요.");
            return;
        }

        try {
            const response = await apiClient.post("/auth/login", {
                userId,
                password,
            });
            const token = response.data.token;
            if (token) {
                const loginTime = new Date().toISOString();
                localStorage.setItem("accessToken", token);
                localStorage.setItem("loginTime",loginTime);
                alert("로그인 완료");
                const isTemp = localStorage.getItem("isTempPassword") === "true";
                if(isTemp){
                    localStorage.removeItem("isTempPassword");
                    location.href = "change-password.html";//페이지 부탁함
                }else{
                    location.href = "index.html";
                }
            }
        } catch (error) {
            alert("로그인 실패: " + (error.response?.data?.message || error.message));
        }
    });
});