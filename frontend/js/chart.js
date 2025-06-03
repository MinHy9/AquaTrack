import { API_BASE, AUTH_HEADER } from './config.js';

let singleChart = null;
let multiChart = null;

export function initCharts() {
    const singleChartEl = document.getElementById('singleChart');
    const multiChartEl = document.getElementById('multiChart');

    if (singleChartEl && !singleChart) {
        singleChart = echarts.init(singleChartEl);
    }
    if (multiChartEl && !multiChart) {
        multiChart = echarts.init(multiChartEl);
    }

    // Load initial data (e.g., daily) for both charts
    loadChartData('daily');

    // Add event listeners for multi chart range buttons
    const multiChartRangeBtns = document.querySelectorAll('.multi-chart-range-btn');
    multiChartRangeBtns.forEach(button => {
        button.addEventListener('click', () => {
            const range = button.getAttribute('data-range');
            loadChartData(range);
            // Update active button style
            multiChartRangeBtns.forEach(btn => btn.classList.remove('bg-primary', 'text-white'));
            button.classList.add('bg-primary', 'text-white');
        });
    });

    // Add event listeners for single chart metric buttons
    const singleChartMetricBtns = document.querySelectorAll('.metric-btn[data-metric]');
    singleChartMetricBtns.forEach(button => {
        button.addEventListener('click', () => {
            const metric = button.getAttribute('data-metric');
            loadSingleChartData(metric, 'daily'); // Assuming single chart defaults to daily
            // Update active button style
            singleChartMetricBtns.forEach(btn => btn.classList.remove('bg-blue-100', 'text-blue-700'));
            button.classList.add('bg-blue-100', 'text-blue-700');
        });
    });
}

export function loadChartData(chartType) {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

    fetch(`${API_BASE}/api/chart/${chartType}?aquariumId=${aquariumId}`, {
        headers: AUTH_HEADER
    })
        .then(res => res.json())
        .then(data => {
            console.log(`Chart data for ${chartType}:`, data);
            let labels = data.categories;
            const temp = data.temperature;
            const ph = data.ph;
            const turbidity = data.turbidity;

            // 시간별 데이터일 경우, 레이블을 'HH:00' 형식으로 변환
            if (chartType === 'hourly') {
                labels = data.categories.map(dateTimeString => {
                    try {
                        // 'YYYY-MM-DD HH:00' 형식 문자열 파싱하여 시간 부분만 추출
                        const date = new Date(dateTimeString);
                        // toLocaleTimeString을 사용하여 로컬 시간으로 변환 및 포맷
                        // 옵션을 조정하여 'HH:MM' 형식만 얻도록 합니다.
                        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
                    } catch (e) {
                        console.error("시간 레이블 변환 오류:", e);
                        return dateTimeString; // 오류 발생 시 원본 문자열 사용
                    }
                });
            }

            if (singleChart && chartType !== 'hourly') { // Assuming single chart is not for hourly
                 singleChart.setOption({
                     title: { text: '수온 변화' }, // Title might need to be dynamic based on selected metric
                     xAxis: { data: labels },
                     yAxis: {},
                     series: [{ name: '수온', type: 'line', data: temp }]
                 });
             }

            if (multiChart) {
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
            }
        });
}

// Function to update charts with real-time data
export function updateChartsRealtime(data) {
    if (multiChart) {
        // Assuming data is a single point { date, temperature, ph, turbidity }
        const option = multiChart.getOption();
        const series = option.series;
        const xAxisData = option.xAxis[0].data;

        // Add new data point
        xAxisData.push(data.date);
        series[0].data.push(data.temperature);
        series[1].data.push(data.ph);
        series[2].data.push(data.turbidity);

        // Optionally remove old data points to keep the chart view manageable
        const maxDataPoints = 30; // Example: keep last 30 data points
        if (xAxisData.length > maxDataPoints) {
            xAxisData.shift();
            series[0].data.shift();
            series[1].data.shift();
            series[2].data.shift();
        }

        multiChart.setOption(option);
    }
    // TODO: Implement real-time update for singleChart if needed
}

export function loadSingleChartData(metricType, chartType) {
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (!aquariumId) return;

     fetch(`${API_BASE}/api/chart/${chartType}?aquariumId=${aquariumId}`, {
        headers: AUTH_HEADER
    })
    .then(res => res.json())
    .then(data => {
        console.log(`Single chart data for ${metricType}/${chartType}:`, data);

        const labels = data.categories;
        let metricData;
        if (metricType === 'temp') metricData = data.temperature;
        else if (metricType === 'ph') metricData = data.ph;
        else if (metricType === 'turbidity') metricData = data.turbidity;

        if (singleChart) {
             singleChart.setOption({
                 title: { text: `${metricType} 변화` }, // Dynamic title
                 xAxis: { data: labels },
                 yAxis: {},
                 series: [{ name: metricType, type: 'line', data: metricData }]
             });
        }
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
