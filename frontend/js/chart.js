import { API_BASE, AUTH_HEADER } from './config.js';

let singleChart, multiChart;

export function initCharts() {
    const statSelect = document.getElementById("stat-select");
    if (!statSelect) {
        console.warn("⚠️ stat-select 요소 없음. initCharts 실행 중단");
        return;
    }

    const singleChartEl = document.getElementById('singleChart');
    const multiChartEl = document.getElementById('multiChart');

    if (!singleChartEl || !multiChartEl) {
        console.warn("⚠️ 차트 요소가 없습니다. initCharts 중단");
        return;
    }

    const chartType = statSelect.value;

    fetch(`${API_BASE}/api/chart/${chartType}`, {
        headers: AUTH_HEADER
    })
        .then(res => res.json())
        .then(data => {
            const labels = data.map(e => e.date);
            const temp = data.map(e => e.temperature);
            const ph = data.map(e => e.ph ?? e.pH);
            const turbidity = data.map(e => e.turbidity);

            singleChart = singleChart || echarts.init(document.getElementById('singleChart'));
            multiChart = multiChart || echarts.init(document.getElementById('multiChart'));

            singleChart.setOption({
                title: { text: '수온 변화' },
                xAxis: { data: labels },
                yAxis: {},
                series: [{ name: '수온', type: 'line', data: temp }]
            });

            multiChart.setOption({
                title: { text: '종합 수질 변화' },
                tooltip: { trigger: 'axis' },
                legend: { data: ['수온', 'pH', '탁도'] },
                xAxis: { data: labels },
                yAxis: {},
                series: [
                    { name: '수온', type: 'line', data: temp },
                    { name: 'pH', type: 'line', data: ph },
                    { name: '탁도', type: 'line', data: turbidity }
                ]
            });
        });
}

document.addEventListener('DOMContentLoaded', () => {
    const statSelect = document.getElementById("stat-select");
    if (statSelect) {
        statSelect.addEventListener("change", initCharts);
    } else {
        console.warn("⚠️ stat-select 요소가 없습니다. 차트 변경 이벤트 바인딩 생략");
    }
});

window.addEventListener('resize', () => {
    singleChart?.resize();
    multiChart?.resize();
});
