import { API_BASE, AUTH_HEADER } from './config.js';

let singleChart, multiChart;
let initialized = false; // 차트 초기화 여부 플래그

export function initCharts(range = 'daily') { // 기본값 'daily' 설정
    if (!initialized) { // 초기화되지 않았으면 차트 객체 생성
        const singleChartEl = document.getElementById('singleChart');
        const multiChartEl = document.getElementById('multiChart');

        if (!singleChartEl || !multiChartEl) {
            console.warn("⚠️ 차트 요소가 없습니다. initCharts 중단");
            return;
        }
        singleChart = echarts.init(singleChartEl);
        multiChart = echarts.init(multiChartEl);
        initialized = true;
    }

    // 데이터 가져오기 및 차트 업데이트
    fetch(`${API_BASE}/api/chart/${range}`, { // 인자로 받은 range 사용
        headers: AUTH_HEADER
    })
    .then(res => res.json())
    .then(data => {
        const labels = data.categories; // 수정: categories 사용
        const temp = data.temperature; // 수정: temperature 사용
        const ph = data.ph ?? data.pH; // 수정: ph 사용
        const turbidity = data.turbidity; // 수정: turbidity 사용

        // singleChart는 선택된 metric에 따라 업데이트되므로 여기서는 multiChart만 업데이트
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

        // singleChart 업데이트 로직 (metrics 버튼 클릭 시 호출될 함수에서 처리)
        // 현재는 multiChart만 업데이트하도록 수정
    });
}

// metrics 버튼 클릭 이벤트 리스너는 index.js에 있으므로 여기서는 제거합니다.
// document.addEventListener('DOMContentLoaded', () => {
// ... existing code ...

window.addEventListener('resize', () => {
    singleChart?.resize();
    multiChart?.resize();
});
