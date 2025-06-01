// 로그인시 로그아웃, 어항등록 보이게 하기
export function renderNavbar() {
    const nav = document.getElementById('nav-links');
    const isLoggedIn =
        localStorage.getItem('loggedIn') === 'true' ||
        sessionStorage.getItem('loggedIn') === 'true';

    if (nav) {
        if (isLoggedIn) {
            nav.innerHTML = `
                <a href="index.html" class="text-primary hover:text-primary/80">대시보드</a>
                <a href="aquarium-register.html" class="text-primary hover:text-primary/80">어항 등록</a>
                <a href="#" class="text-primary hover:text-primary/80" id="logout-btn">로그아웃</a>
            `;
        } else {
            nav.innerHTML = `
                <a href="login.html" class="text-primary hover:text-primary/80">로그인</a>
            `;
        }

        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                localStorage.removeItem('loggedIn');
                localStorage.removeItem('token');
                sessionStorage.removeItem('loggedIn');
                sessionStorage.removeItem('token');
                alert('로그아웃 되었습니다.');
                location.href = 'login.html';
            });
        }
    }
}
