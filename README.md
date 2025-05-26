# AquaTrack
심화캡스톤 AquaTrack

[Backend]
1. User 도메인
- 역할: 회원가입, 로그인, 사용자 인증 및 정보 관리
- 주요 클래스: UserController, UserService, UserRepository, User
- 기타: JWT 기반 인증 (JwtTokenProvider, JwtAuthenticationFilter, SecurityConfig)

2. Aquarium 도메인
- 역할: 어항 등록, 기준 수치 관리, 사용자 연동
- 주요 클래스: AquariumController, AquariumService, AquariumRepository, Aquarium
- DTO: AquariumRequest, AquariumThresholdUpdateRequest

3. Sensor 도메인
- 역할: 수온, pH, 탁도 등의 센서 데이터 수신 및 저장
- 주요 클래스: WaterQualityLogController, WaterQualityLogService, WaterQualityLogRepository, WaterQualityLog
- MQTT 수신 처리: MqttSensorSubscriber → DB 저장

4. Stats 도메인
- 역할: 일간/주간/월간 평균 통계 제공
- 주요 클래스: StatsController, StatsService
- DTO: DailySensorStatResponse 등

5. DeviceControl 도메인
- 역할: 냉각팬, 환수펌프 수동 제어 (MQTT 발행)
- 주요 클래스: DeviceControlController, DeviceControlService
- DTO: DeviceControlRequest

6. Feeding 도메인
- 역할: 자동/수동 먹이 급여, 급여 스케줄 관리
- 주요 클래스: FeedingController, FeedingService, FeedingSchedule, FeedingScheduleRepository
- 기타: AutoFeedingState, FeedingStateService → 자동 급여 상태 관리

7. Alert/Notification 도메인
- 역할: 이상 수치 발생 시 SMS 알림 전송 및 중복 방지
- 주요 클래스:
    - Alert: Alert 엔티티, AlertType 열거형
    - NotificationService, SmsService (Solapi 연동)
    - ScheduledNotificationSender, AlertStatusTracker (10분 이상 지속 시 재알림)

8. Chart 도메인
- 역할: 센서 데이터 시각화용 데이터 응답 처리
- 주요 클래스: ChartController, ChartDataResponse

9. Dashboard 도메인
- 역할: 사용자 대시보드 응답 DTO 제공
- 주요 클래스: DashboardController
- DTO: AquariumStatusResponse, AlertHistoryResponse, LatestSensorDataResponse 등

10. MQTT 통신 도메인
- 역할: AWS IoT Core와 MQTT 발행/구독 처리
- 주요 클래스:
    - MqttService: 메시지 발행
    - MqttSensorSubscriber: 센서 데이터 수신
    - MqttConfig: 연결 설정
    - AwsIotMqttUtil: 메시지 구성 및 유틸 기능

