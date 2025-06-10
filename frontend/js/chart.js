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

    // 초기 데이터 로드
    const aquariumId = localStorage.getItem('selectedAquariumId');
    if (aquariumId) {
        loadChartData('daily');
        loadSingleChartData('temp', 'daily');
    }

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
    const boardId = localStorage.getItem('selectedAquariumBoardId');
    if (!boardId) {
        console.warn("⚠️ 선택된 어항의 보드 ID가 없습니다.");
        if (multiChart) {
             multiChart.clear();
        }
        return;
    }

    fetch(`${API_BASE}/api/chart/${chartType}?boardId=${boardId}`, {
        headers: AUTH_HEADER
    })
        .then(res => {
            if (!res.ok) {
                res.text().then(text => console.error(`차트 데이터 로드 실패 (${chartType}):`, text));
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            console.log(`Chart data for ${chartType}:`, data);
            const labels = data.map(item => item.dateLabel);
            const temp = data.map(item => item.temperatureAvg);
            const ph = data.map(item => item.phavg);
            const turbidity = data.map(item => item.turbidityAvg);

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
                }, true);
            }
        })
        .catch(error => {
            console.error(`차트 데이터 로딩 중 오류 발생 (${chartType}):`, error);
            if (multiChart) {
                multiChart.clear();
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
    const boardId = localStorage.getItem('selectedAquariumBoardId');
    if (!boardId) {
         console.warn(`⚠️ 선택된 어항의 보드 ID가 없습니다. (${metricType}/${chartType})`);
         if (singleChart) {
             singleChart.clear();
         }
         return;
    }

    fetch(`${API_BASE}/api/chart/${chartType}?boardId=${boardId}`, {
        headers: AUTH_HEADER
    })
    .then(res => {
         if (!res.ok) {
             res.text().then(text => console.error(`단일 차트 데이터 로드 실패 (${metricType}/${chartType}):`, text));
             throw new Error(`HTTP error! status: ${res.status}`);
         }
         return res.json();
     })
    .then(data => {
        console.log(`Single chart data for ${metricType}/${chartType}:`, data);

        const labels = data.map(item => item.dateLabel);
        let metricData;
        let title;

        if (metricType === 'temp') {
            metricData = data.map(item => item.temperatureAvg);
            title = '수온 변화';
        } else if (metricType === 'ph') {
            metricData = data.map(item => item.phavg);
            title = 'pH 변화';
        } else if (metricType === 'turbidity') {
            metricData = data.map(item => item.turbidityAvg);
            title = '탁도 변화';
        }

        if (singleChart) {
            singleChart.setOption({
                title: { text: title },
                tooltip: { trigger: 'axis' },
                xAxis: { data: labels },
                yAxis: {},
                series: [{ name: title, type: 'line', data: metricData }]
            }, true);
        }
    })
    .catch(error => {
        console.error(`단일 차트 데이터 로딩 중 오류 발생 (${metricType}/${chartType}):`, error);
        if (singleChart) {
            singleChart.clear();
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
