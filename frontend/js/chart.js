import { API_BASE, AUTH_HEADER } from './config.js';

let singleChart, multiChart;

export function initCharts() {
    const chartType = document.getElementById("stat-select").value;

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
    document.getElementById("stat-select").addEventListener("change", initCharts);
});

window.addEventListener('resize', () => {
    singleChart?.resize();
    multiChart?.resize();
});
