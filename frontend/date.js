const timeRefreshButton = document.querySelector("#time-refresh-button");
const dateRefreshButton = document.querySelector("#date-refresh-button");

function updateTime() {
    const today = new Date();
    const hours = today.getHours().toString().padStart(2, '0');
    const minutes = today.getMinutes().toString().padStart(2, '0');
    const formattedTime = `${hours}:${minutes}`;
    document.getElementById("current-time").innerText = formattedTime;
}

function updateDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = (today.getMonth() + 1).toString().padStart(2, '0');
    const day = today.getDate().toString().padStart(2, '0');
    const formattedDate = `${year}.${month}.${day}`;
    document.getElementById("today-date").innerText = formattedDate;
}

timeRefreshButton.addEventListener("click", updateTime);
dateRefreshButton.addEventListener("click", updateDate);

document.addEventListener("DOMContentLoaded", () => {
    updateTime();
    updateDate();
});
