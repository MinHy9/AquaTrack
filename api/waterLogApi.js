
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

function getTimeLabel(date = new Date()) {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
}

let singleChart;
document.addEventListener("DOMContentLoaded",async()=>{
    try{
        singleChart = echarts.init(document.getElementById('singleChart'));
        makeChartForm([],[]);
    }catch(error){
        console.error("에러");
    }
})
//실시간으로 websocket 통신 통해 mqtt 서버로부터 받아와서 그림
// 초 단위로 그리고 배열의 길이가 6보다 커지면 맨 왼쪽에 있는 거 지우고 새로운 데이터 추가하는 방식으로 그려짐
// 그래서 6초 이후로는 계속 맨앞에꺼 지우고 뒤에 추가하고 이런식으로 게속 그려지게 코드 구성성
document.addEventListener("DOMContentLoaded",async()=>{

    try{
        const multiChart = echarts.init(document.getElementById('multiChart'));
        const multiOption = {
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
                data: [],
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
                    name: '수온',
                    type: 'line',
                    data: [],
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
                },
                {
                    name: 'pH',
                    type: 'line',
                    data: [],
                    symbol: 'none',
                    smooth: true,
                    lineStyle: {
                        width: 3,
                        color: 'rgba(141, 211, 199, 1)'
                    },
                    areaStyle: {
                        color: {
                            type: 'linear',
                            x: 0, y: 0, x2: 0, y2: 1,
                            colorStops: [{
                                offset: 0, color: 'rgba(141, 211, 199, 0.2)'
                            }, {
                                offset: 1, color: 'rgba(141, 211, 199, 0.05)'
                            }]
                        }
                    }
                },
                {
                    name: '탁도',
                    type: 'line',
                    data: [],
                    symbol: 'none',
                    smooth: true,
                    lineStyle: {
                        width: 3,
                        color: 'rgba(252, 141, 98, 1)'
                    },
                    areaStyle: {
                        color: {
                            type: 'linear',
                            x: 0, y: 0, x2: 0, y2: 1,
                            colorStops: [{
                                offset: 0, color: 'rgba(252, 141, 98, 0.2)'
                            }, {
                                offset: 1, color: 'rgba(252, 141, 98, 0.05)'
                            }]
                        }
                    }
                }
            ]
        };

        let xAxisLabels = [];
        let temperatureData = [];
        let phData = [];
        let turbidityData = [];

        multiChart.setOption(multiOption);
        stompClient.subscribe(`/aquatrack/sensor`, function (message) {
            const data = JSON.parse(message.body);
            const recordedTime = new Date(data.recordedAt);
            let timeLabel = getTimeLabel(recordedTime);
           
            if (xAxisLabels.length >= 6) {
                xAxisLabels.shift();
                temperatureData.shift();
                phData.shift();
                turbidityData.shift();
            }

            xAxisLabels.push(timeLabel);
            temperatureData.push(data.temperature);
            phData.push(data.ph);
            turbidityData.push(data.turbidity);

            multiChart.setOption({
                xAxis: [{ data: xAxisLabels }],
                series: [
                  { data: temperatureData },
                  { data: phData },
                  { data: turbidityData }
                ]
              });;

            document.querySelector('.temp-card .value').textContent = data.temperature.toFixed(1);
            document.querySelector('.ph-card .value').textContent = data.ph.toFixed(1);
            document.querySelector('.turb-card .value').textContent = data.turbidity.toFixed(1);
        
        });
        
    }catch(error){
        console.error("데이터 로딩 실패");        
    }
})
