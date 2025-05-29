import apiClient from './apiClient.js'

let singleChart;
document.addEventListener("DOMContentLoaded",async()=>{
    try{
        singleChart = echarts.init(document.getElementById('singleChart'));
        makeChartForm([],[]);
    }catch(error){
        console.error("에러");
    }
})

async function getWaterStats(range){
    const dailyStats = await apiClient.get(`/chart/${range}`);
    return dailyStats.data;
}

document.querySelectorAll('.metric-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.metric-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        tryDrawChart(); 
    });
});

// 기간 버튼 클릭 이벤트
document.querySelectorAll('.range-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.range-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        tryDrawChart(); 
    });
});



function getMonthWeekLabel(weekLabel) {
    // 예: "2025-W13" 또는 "2025W13"
    // 연도와 주차 분리
    const match = weekLabel.match(/(\d{4})-?W(\d{1,2})/);
    if (!match) return weekLabel; // 패턴 아니면 그냥 리턴

    const year = parseInt(match[1], 10);
    const weekOfYear = parseInt(match[2], 10);

    // 해당 연도의 1월 1일 날짜
    const firstDayOfYear = new Date(year, 0, 1);

    // ISO 주차 계산을 위해 1월 4일 기준으로 주 계산 (ISO 8601 기준)
    const jan4 = new Date(year, 0, 4);
    const dayOfWeek = jan4.getDay() || 7; // 일요일 = 7

    // 1년의 첫 주 월요일 구하기
    const firstMonday = new Date(jan4);
    firstMonday.setDate(jan4.getDate() - (dayOfWeek - 1));

    // 원하는 주의 월요일 날짜 구하기
    const targetMonday = new Date(firstMonday);
    targetMonday.setDate(firstMonday.getDate() + (weekOfYear - 1) * 7);

    // targetMonday 기준으로 몇 월인지
    const month = targetMonday.getMonth() + 1; // 1~12월

    // 그 달 1일 날짜
    const firstOfMonth = new Date(year, month - 1, 1);

    // targetMonday와 그 달 1일 차이 (일 수)
    const diffDays = Math.floor((targetMonday - firstOfMonth) / (1000 * 60 * 60 * 24));

    // 몇 주차인지 계산 (0~6일은 1주차, 7~13일은 2주차 ...)
    const weekOfMonth = Math.floor(diffDays / 7) + 1;

    return `${month}월 ${weekOfMonth}주차`;
}
// 기간 ,(온도,탁도, ph 버튼 중 한 개) 총 두개 버튼 선택해야 그래프 그려지게 만듬
// 좌우 버튼 누를 떄 일, 주, 월 기간에 따라 통계치가 다르게 나오게 하고 싶은데 제 머리로는 못하겠어요.... 가능하면 부탁드려요..
async function tryDrawChart(){
    const activeMetric = document.querySelector('.metric-btn.active');
    const activeRange = document.querySelector('.range-btn.active');

    if (!activeMetric || !activeRange) return;
    
    const metric = activeMetric.dataset.metric;
    const range = activeRange.dataset.range;

    const waterData = await getWaterStats(range);
    
    const timeLabel = waterData.map(data => {
        const label = data.dateLabel;
         if(typeof label === 'string'){
            if(label.includes('-')){
                const parts = label.split('-');
                const month = parseInt(parts[1], 10);
                return `${month}월`;
            }else{
                return getMonthWeekLabel(label);
            }
         }else{
            return label;
         }
    })
    
    switch(metric){
        case "turbidity":
            const turbidityData = waterData.turbidity;
            makeChartForm(timeLabel,turbidityData);
            break;
        case "temperature" :
            const temperatureData = waterData.temperature;
            makeChartForm(timeLabel,temperatureData);
            break;
        case "ph":
            const phData = waterData.ph;
            makeChartForm(timeLabel,phData);
            break;
        default:
            console.error("그래프 그릴 수 없습니다.");
    }

}
//차트 틀 만듬
function makeChartForm(time, waterData){
    const singleOption = {
        animation: false,
            grid: {
                left: '3%',
                right: '3%',
                bottom: '3%',
                top: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                data: time,
                axisLine: {
                    lineStyle: {
                        color: '#ddd'
                    }
                },
                axisLabel: {
                    color: '#666'
                }
            },
            yAxis: {
                type: 'value',
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                splitLine: {
                    lineStyle: {
                        color: '#eee'
                    }
                },
                axisLabel: {
                    color: '#666'
                }
            },
            tooltip: {
                trigger: 'axis',
                backgroundColor: 'rgba(255, 255, 255, 0.9)',
                borderColor: '#eee',
                borderWidth: 1,
                textStyle: {
                    color: '#333'
                }
            },
            series: [
                {
                    name: 'pH',
                    type: 'line',
                    data: waterData,
                    symbol: 'none',
                    smooth: true,
                    lineStyle: {
                        width: 3,
                        color: 'rgba(87, 181, 231, 1)'
                    },
                    areaStyle: {
                        color: {
                            type: 'linear',
                            x: 0, y: 0, x2: 0, y2: 1,
                            colorStops: [{
                                offset: 0, color: 'rgba(87, 181, 231, 0.2)'
                            }, {
                                offset: 1, color: 'rgba(87, 181, 231, 0.05)'
                            }]
                        }
                    }
                }
            ]
        };
        singleChart.setOption(singleOption);
}
