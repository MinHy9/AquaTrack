//#define DEBUG_R4_SERIAL  // 필요 시 주석 해제하여 디버깅 메시지 활성화

#include <Arduino.h>
#include <Servo.h>
#include <OneWire.h>
#include <DallasTemperature.h>

// ======== 서보·쿨러·펌프 제어 관련 ========
Servo servo;
const int servoPin   = 12; // 서보모터 제어 핀 (Feed)
const int coolerPin  = 5;  // 쿨러(팬) 제어 핀 (Cooler)
const int pumpPin    = 3;  // 워터 펌프 제어 핀 (Pump)

// UART1 명령 구분자
const char CMD_FEED   = 'F';  // Feed (서보 각도 증가)
const char CMD_COOLER = 'C';  // Cooler (0=OFF, 1=ON)
const char CMD_PUMP   = 'P';  // Pump   (0=OFF, 1=ON)

bool    isServoAttached = false; // 서보가 현재 attach 상태인지 플래그
int     currentAngle    = 0;     // 서보가 위치한 현재 각도 (0 ~ 180)
const int angleStep     = 50;    // 한 번에 증가시킬 각도 단위 (예: 50° 증가)

// ======== pH 센서 관련 ========
const int pHSensePin    = A0;   // pH 센서가 연결된 아날로그 핀
const int pHSamples     = 10;   // pH 측정 시 평균을 위해 읽을 샘플 개수
float calculatePH(float voltage) {
  // pH 센서 모델에 맞춘 보정식 (필요 시 센서 매뉴얼에 맞춰 수정)
  return -5.36 * voltage + 21.69;
}

// ======== 탁도 센서 관련 ========
const int turbidityPin  = A2;   // 탁도 센서가 연결된 아날로그 핀
const float V_AIR    = 3.04;    // 공기 상태 기준 전압 (예시)
const float V_WATER  = 3.98;    // 깨끗한 물 상태 기준 전압 (예시)
const float V_MAX    = 5.00;    // 센서 최대 전압 (아두이노 5V 기준)

// ======== DS18B20 수온 센서 관련 ========
#define ONE_WIRE_BUS 2           // DS18B20 데이터 라인(OneWire) 연결 핀
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature tempSensor(&oneWire);

// === 전역 버퍼 ===
// Serial1(ESP32)로부터 들어오는 남은 개행문자 제거 등에 사용
String serial1Line = "";

// 센서 데이터 전송 주기 관리를 위한 변수
unsigned long lastSensorTime = 0;
const unsigned long sensorInterval = 5000; // 10초 (밀리초 단위)

void setup() {
  // ====== 시리얼 초기화 ======
  // R4 ↔ ESP32 통신: Serial1(9600) 사용
  Serial1.begin(9600);
  // R4 디버깅용 USB 시리얼: Serial(115200)
  Serial.begin(115200);

#ifdef DEBUG_R4_SERIAL
  Serial.println("▶ R4: setup() 시작");
#endif

  // ====== 서보 초기 위치 설정 후 detach ======
  servo.attach(servoPin);
  isServoAttached = true;
  currentAngle = 0;
  servo.write(currentAngle);  // 초기 0도 세팅
  delay(500);                 // 서보가 목표 각도까지 도달할 시간
  servo.detach();
  isServoAttached = false;

#ifdef DEBUG_R4_SERIAL
  Serial.println("▶ R4: 서보 초기화 완료 (0° → detach 상태)");
#endif

  // ====== 팬·펌프 핀 초기화 ======
  pinMode(coolerPin, OUTPUT);
  digitalWrite(coolerPin, LOW);  // 쿨러 초기 OFF

  pinMode(pumpPin, OUTPUT);
  digitalWrite(pumpPin, HIGH);   // 펌프 초기 OFF (회로에 따라 HIGH/LOW 반전 가능)

#ifdef DEBUG_R4_SERIAL
  Serial.println("▶ R4: 쿨러/펌프 핀 초기화 완료");
#endif

  // ====== DS18B20 온도 센서 초기화 ======
  tempSensor.begin();
#ifdef DEBUG_R4_SERIAL
  Serial.println("▶ R4: DS18B20 초기화 완료");
#endif

  // ====== 아날로그 입력 해상도 설정 (필요 시) ======
  #if defined(ARDUINO_ARCH_ESP32)
    // R4가 ESP32 아키텍처가 아니라면 이 부분은 건너뜁니다.
    analogReadResolution(12);       // 0~4095 (12비트)
    analogSetAttenuation(ADC_11db); // 입력 전압 범위 최대 3.3V
#ifdef DEBUG_R4_SERIAL
    Serial.println("▶ R4: analogReadResolution 설정됨 (ESP32 기준)");
#endif
  #endif

#ifdef DEBUG_R4_SERIAL
  Serial.println("▶ R4: setup() 완료");
#endif
}

void loop() {
  // ===============================
  // 1) Serial1(=ESP32)로부터 명령 수신·파싱 (즉시 처리)
  // ===============================
  if (Serial1.available()) {
    char cmd = Serial1.read();
#ifdef DEBUG_R4_SERIAL
    Serial.print("[R4:Serial1] Rcv cmd: ");
    Serial.println(cmd);
#endif

    switch (cmd) {
      case CMD_FEED: {
        // ==== (가장 중요) angleStep만큼 증가시키는 로직 ====
        currentAngle += angleStep;
        // 150을 초과하면 0으로 다시 리셋
        // (원래 180을 기준으로 했지만, angleStep=50이므로 150초과 시 0 설정)
        if (currentAngle > 150) {
          currentAngle = 0;
        }
#ifdef DEBUG_R4_SERIAL
        Serial.print("[R4] New Feed Angle: ");
        Serial.println(currentAngle);
#endif

        // 1) attach → 2) write → 3) 짧게 대기 → 4) detach
        if (!isServoAttached) {
          servo.attach(servoPin);
          isServoAttached = true;
        }
        servo.write(currentAngle);
        delay(500);        // 서보가 목표 각도까지 도달할 시간 (약 500ms)
        servo.detach();
        isServoAttached = false;
#ifdef DEBUG_R4_SERIAL
        Serial.println("[R4] Servo detached (PWM OFF)");
#endif
        break;
      }

      case CMD_COOLER: {
        // “C” 다음에 들어오는 0/1을 읽어서 쿨러 ON/OFF
        int state = Serial1.parseInt();
#ifdef DEBUG_R4_SERIAL
        Serial.print("[R4] Cooler state: ");
        Serial.println(state);
#endif
        digitalWrite(coolerPin, state ? HIGH : LOW);
        break;
      }

      case CMD_PUMP: {
        // “P” 다음에 들어오는 0/1을 읽어서 펌프 ON/OFF
        int state = Serial1.parseInt();
#ifdef DEBUG_R4_SERIAL
        Serial.print("[R4] Pump state: ");
        Serial.println(state);
#endif
        // (회로 논리에 따라 ON=LOW, OFF=HIGH 설정)
        digitalWrite(pumpPin, state ? LOW : HIGH);
        break;
      }

      default:
        // CMD 외에 들어온 문자(예: 개행 등) 처리
        break;
    }

    // 남은 개행 문자(‘\n’, ‘\r’)만 비우기
    while (Serial1.available()) {
      char junk = Serial1.peek();
      if (junk == '\n' || junk == '\r') {
        Serial1.read();
      } else {
        break;
      }
    }
  }

  // ===============================
  // 2) 센서 측정 및 전송 (10초 주기로 실행)
  // ===============================
  unsigned long currentMillis = millis(); // 현재 시간 확인

  if (currentMillis - lastSensorTime >= sensorInterval) {
    // 마지막 전송 시점에서 10초가 지났으면
    lastSensorTime = currentMillis; // 마지막 전송 시간 업데이트

    // pH 센서 측정
    long sumPH = 0;
    for (int i = 0; i < pHSamples; i++) {
      #if defined(ARDUINO_ARCH_ESP32)
        sumPH += analogRead(pHSensePin);   // ESP32: 0~4095
      #else
        sumPH += analogRead(pHSensePin);   // AVR/ARM 계열: 0~1023
      #endif
      delay(10);  // 샘플링 간 10ms 대기 (총 100ms)
    }
    float rawPH = sumPH / float(pHSamples);
    #if defined(ARDUINO_ARCH_ESP32)
      float voltPH = rawPH * (5.0f / 4095.0f);   // ESP32 12비트 기준
    #else
      float voltPH = rawPH * (5.0f / 1023.0f);   // AVR/ARM 10비트 기준
    #endif
    float pH = calculatePH(voltPH);

    // 탁도 센서 측정 및 보정
    #if defined(ARDUINO_ARCH_ESP32)
      int rawTurb   = analogRead(turbidityPin);
      float voltOri = rawTurb * (5.0f / 4095.0f);
    #else
      int rawTurb   = analogRead(turbidityPin);
      float voltOri = rawTurb * (5.0f / 1023.0f);
    #endif
    float voltCal = (voltOri - V_AIR) * (V_MAX / (V_WATER - V_AIR));
    voltCal = constrain(voltCal, 0.0f, V_MAX);

    // DS18B20 수온 센서 측정 (약 750ms 소요)
    tempSensor.requestTemperatures();
    float tempC = tempSensor.getTempCByIndex(0);

    // Serial1(ESP32)로 CSV 형식 전송
    // "temperature,ph,turbidity\n"
    Serial1.print(tempC, 2);
    Serial1.print(',');
    Serial1.print(pH, 2);
    Serial1.print(',');
    Serial1.println(voltCal, 3);

#ifdef DEBUG_R4_SERIAL
    Serial.print("[R4] CSV Published → ");
    Serial.print(tempC, 2);
    Serial.print(',');
    Serial.print(pH, 2);
    Serial.print(',');
    Serial.println(voltCal, 3);
#endif
  }

  // loop 함수는 빠르게 반복되며 Serial1 명령을 계속 확인합니다.
}
