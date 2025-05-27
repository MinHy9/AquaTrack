import apiClient from './apiClient.js'

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('signup-button').onclick = async () => {
        const name = document.getElementById('name').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();
        const confirmPassword = document.getElementById('confirm-password').value.trim();
        const phone = document.getElementById('phone').value.trim();
        const agree = document.getElementById('agree-terms').checked;

        let valid = true;
        document.querySelectorAll('.error-message').forEach(e => e.style.display = 'none');

        if (email === password) {
            document.getElementById('password-same-error').style.display = 'block';
            valid = false;
        }

        if (password !== confirmPassword) {
            alert("비밀번호가 일치하지 않습니다. 다시 입력해주세요");
            valid = false;
        }

        if (!agree) {
            alert('이용약관에 동의해주세요.');
            valid = false;
        }

        if (!valid) return; 

        try {
            const response = await apiClient.post("/register", {
                name, email, password, phone
            });
            alert('회원가입이 완료되었습니다!');
            window.location.href = 'login.html';
        } catch (error) {
            const message = error.response?.data?.message || "";

            if (message.includes("비밀번호는 영문자")) {
                document.getElementById('password-format-error').style.display = 'block';
            }

            if (message.includes("같은 문자를 4번 이상")) {
                document.getElementById("password-repeat-error").style.display = 'block';
            }

            // 서버 오류에 대한 추가 안내
            if (!message) {
                alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
        }
    };
});
