#include <WiFi.h>
#include <WebServer.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <Preferences.h>

// === AWS IoT / MQTT 정보 ===
const char* mqttServer = "a2cjg75sa1duja-ats.iot.us-east-1.amazonaws.com";
const int   mqttPort   = 8883;

// 인증서(파일로부터 복사해둔 상수들)
#include "cert.h"      // DEVICE_CERT
#include "privateKey.h"// DEVICE_KEY
#include "caCert.h"    // AWS_ROOT_CA

// === AP 모드 SSID/PW ===
const char* apSSID = "ESP32_Setup";
const char* apPW   = "setup1234";  // AP 접속 비밀번호

// === Arduino R4 시리얼 제어용 핀 ===
#define ESP32_RX_PIN 16   // UART2 RX (ESP32가 수신)
#define ESP32_TX_PIN 17   // UART2 TX (ESP32가 송신)

// === 공통 전역 객체 및 변수 ===
WebServer webServer(80);            // HTTP 서버 포트 80
WiFiClientSecure net;              
PubSubClient client(net);           // MQTT 클라이언트
Preferences preferences;            // NVS 저장용

String savedSSID     = "";          // NVS에서 읽어온 SSID
String savedPassword = "";          // NVS에서 읽어온 PW
String boardId       = "";          // 보드 ID

String formSSID      = "";          // 폼에서 받은 SSID
String formPassword  = "";          // 폼에서 받은 PW
bool   hasWiFiConfig = false;       // 폼 제출 플래그

// === 원래 코드 전역 변수 ===
// R4에서 읽은 센서 데이터를 저장할 버퍼
String serialBuffer = "";

// MAC 주소로부터 고유한 보드 ID 생성
String generateBoardId() {
  uint8_t mac[6];
  WiFi.macAddress(mac);
  
  // MAC 주소를 16진수 문자열로 변환
  char macStr[13];
  snprintf(macStr, sizeof(macStr), "%02X%02X%02X%02X%02X%02X",
           mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
  
  return String("ESP32_") + String(macStr);
}

// --- MQTT 수신 콜백 (원래 코드) ---
void mqttCallback(char* topic, byte* payload, unsigned int length) {
  String msg;
  for (unsigned int i = 0; i < length; i++) {
    msg += (char)payload[i];
  }
  msg.trim();
  Serial.println("RECV [" + String(topic) + "] " + msg);

  // 급식 제어
  String feedTopic = String("aquatrack/") + boardId + "/feeding";
  if (String(topic) == feedTopic && msg == "feed_now") {
    Serial.println("▶ feed_now 수신 → 서보 45° 회전");
    Serial2.print("F45\n");     // R4에 'F45' 명령 전송
  }

  // 냉각팬 제어
  String coolerTopic = String("aquatrack/") + boardId + "/cooler";
  if (String(topic) == coolerTopic) {
    if (msg == "on") {
      Serial.println("▶ 냉각팬 ON");
      Serial2.print("C1\n");    // R4에 'C1' 명령 전송
    } else if (msg == "off") {
      Serial.println("▶ 냉각팬 OFF");
      Serial2.print("C0\n");    // R4에 'C0' 명령 전송
    }
  }

  // 환수 펌프 제어
  String pumpTopic = String("aquatrack/") + boardId + "/pump";
  if (String(topic) == pumpTopic) {
    if (msg == "on") {
      Serial.println("▶ 환수 펌프 ON");
      Serial2.print("P1\n");    // R4에 'P1' 명령 전송
    } else if (msg == "off") {
      Serial.println("▶ 환수 펌프 OFF");
      Serial2.print("P0\n");    // R4에 'P0' 명령 전송
    }
  }
}

// === 원래 코드: AWS IoT(또는 MQTT 브로커) 연결 함수 ===
void connectAWS() {
  net.setCACert(AWS_ROOT_CA);
  net.setCertificate(DEVICE_CERT);
  net.setPrivateKey(DEVICE_KEY);

  client.setServer(mqttServer, mqttPort);
  client.setCallback(mqttCallback);

  Serial.print("🔐 MQTT 연결 중...");
  while (!client.connect("ESP32FishTank")) {
    Serial.print("❌ 실패. 다시 시도...");
    delay(1000);
  }
  Serial.println("✅ 연결 성공!");

  // 토픽 구독
  String feedTopic   = String("aquatrack/") + boardId + "/feeding";
  String coolerTopic = String("aquatrack/") + boardId + "/cooler";
  String pumpTopic   = String("aquatrack/") + boardId + "/pump";

  client.subscribe(feedTopic.c_str(),   1);
  client.subscribe(coolerTopic.c_str(), 1);
  client.subscribe(pumpTopic.c_str(),   1);

  Serial.println("구독 완료: " + feedTopic);
  Serial.println("구독 완료: " + coolerTopic);
  Serial.println("구독 완료: " + pumpTopic);
}

// === 함수 선언 (AP + NVS 통합) ===
void handleRoot();
void handleFormSubmit();
void handleNotFound();
void startAPMode();
void tryConnectSavedWiFi();
void connectToMQTT();

// === setup() ===
void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("\n--- ESP32 부팅 ---");

  // ★ R4(Arduino R4) 쪽 UART2 초기화(ESP32 ↔ R4 통신)
  Serial2.begin(9600, SERIAL_8N1, ESP32_RX_PIN, ESP32_TX_PIN);

  // 1) NVS 시작
  preferences.begin("wifi", false);

  // 2) 저장된 SSID/PW 읽어오기
  savedSSID     = preferences.getString("ssid", "");
  savedPassword = preferences.getString("pw", "");
  boardId       = preferences.getString("boardId", "");
  preferences.end();

  // 보드 ID가 없으면 MAC 주소로 생성
  if (boardId.length() == 0) {
    boardId = generateBoardId();
    Serial.printf("🔑 생성된 보드 ID: %s\n", boardId.c_str());
    
    // NVS에 저장
    preferences.begin("wifi", false);
    preferences.putString("boardId", boardId);
    preferences.end();
  }

  // 3) 저장된 SSID/PW가 있으면 STA 모드로 연결 시도
  if (savedSSID.length() > 0) {
    Serial.printf("🔍 저장된 Wi-Fi 확인: SSID=\"%s\"\n", savedSSID.c_str());
    tryConnectSavedWiFi();

    // 연결되었으면 MQTT 연결로 넘어감
    if (WiFi.status() == WL_CONNECTED) {
      Serial.println("✅ 이전에 저장된 Wi-Fi 연결 성공");
      connectAWS();
      return;
    }
    Serial.println("⚠️ 저장된 Wi-Fi 연결 실패 → AP 모드로 전환");
  } else {
    Serial.println("ℹ️ 저장된 Wi-Fi 정보가 없음 → AP 모드로 전환");
  }

  // 4) (저장된 정보가 없거나 연결 실패 시) AP 모드 시작
  startAPMode();
}

// === loop() ===
void loop() {
  // AP 모드 중이라면 웹서버 처리
  if (WiFi.getMode() == WIFI_AP) {
    webServer.handleClient();
  }

  // STA 모드인데 MQTT 미연결 상태라면 재시도
  if (WiFi.status() == WL_CONNECTED && !client.connected()) {
    connectAWS();
  }

  // MQTT 루프
  if (client.connected()) {
    client.loop();
  }

  // === Serial2(=R4) 로부터 센서 데이터 수신 및 MQTT 퍼블리시 (원래 코드) ===
  if (WiFi.status() == WL_CONNECTED && client.connected()) {
    while (Serial2.available()) {
      char c = Serial2.read();
      if (c == '\n') {
        // 한 줄(line)이 완성되면 파싱 처리
        String line = serialBuffer;
        serialBuffer = "";  // 버퍼 초기화

        line.trim();        // 앞뒤 공백 제거
        if (line.length() > 0) {
          // 예: "25.30,7.12,1.234"
          float tempVal = 0, phVal = 0, turbVal = 0;
          int idx1 = line.indexOf(',');
          int idx2 = line.lastIndexOf(',');

          if (idx1 > 0 && idx2 > idx1) {
            tempVal = line.substring(0, idx1).toFloat();
            phVal   = line.substring(idx1 + 1, idx2).toFloat();
            turbVal = line.substring(idx2 + 1).toFloat();

            // JSON payload 구성
            String payload = "{"
              "\"boardId\":\"" + boardId + "\","
              "\"temperature\":" + String(tempVal, 2) + ","
              "\"ph\":"          + String(phVal, 2) + ","
              "\"turbidity\":"   + String(turbVal, 3) +
            "}";

            // MQTT 퍼블리시
            String sensorTopic = String("aquatrack/sensor");
            if (client.publish(sensorTopic.c_str(), payload.c_str(), true)) {
              Serial.println("PUBLISH ▶ " + payload);
            } else {
              Serial.println("❌ PUBLISH 실패");
            }
          } else {
            // 파싱 실패(콤마 개수가 부족)
            Serial.println("⚠️ 잘못된 포맷: " + line);
          }
        }
      } else {
        // '\n'이 아닐 때는 버퍼에 누적
        serialBuffer += c;
        // (예: 너무 길어지지 않도록 최대 길이 100자 제한)
        if (serialBuffer.length() > 100) {
          serialBuffer = "";
        }
      }
    }
  }
}

// === AP 모드 시작 ===
void startAPMode() {
  Serial.println("▶ AP 모드 시작");
  WiFi.mode(WIFI_AP);
  WiFi.softAP(apSSID, apPW);

  Serial.printf("  • AP SSID: \"%s\", PW: \"%s\"\n", apSSID, apPW);
  Serial.println("  • 브라우저에서 192.168.4.1 접속 후 Wi-Fi 정보 입력");

  // HTTP 서버 핸들러 설정
  webServer.on("/", HTTP_GET, handleRoot);
  webServer.on("/submit", HTTP_POST, handleFormSubmit);
  webServer.onNotFound(handleNotFound);
  webServer.begin();
  Serial.println("  • HTTP 서버 시작됨");
}

// === 저장된 Wi-Fi로 연결 시도 ===
void tryConnectSavedWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(savedSSID.c_str(), savedPassword.c_str());

  Serial.printf("▶ Wi-Fi 연결 시도: SSID=\"%s\"\n", savedSSID.c_str());
  unsigned long start = millis();
  while (millis() - start < 10000 && WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  if (WiFi.status() == WL_CONNECTED) {
    Serial.printf("✅ Wi-Fi 연결됨! IP: %s\n", WiFi.localIP().toString().c_str());
  } else {
    Serial.println("❌ Wi-Fi 연결 실패");
  }
}

// === 루트("/") 요청 시 HTML 폼 반환 ===
void handleRoot() {
  String currentBoardId = boardId.length() > 0 ? boardId : generateBoardId();
  
  String html = R"rawliteral(
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Wi-Fi 설정</title>
    <style>
      body { font-family: Arial, sans-serif; margin: 30px; }
      input, button { padding: 8px; margin: 5px 0; width: 100%; }
      .container { max-width: 400px; margin: auto; }
      .hint { font-size: 0.9em; color: #555; }
      .board-id { 
        background-color: #f0f0f0; 
        padding: 10px; 
        border-radius: 4px;
        margin: 10px 0;
        word-break: break-all;
      }
      .copy-btn {
        background-color: #4CAF50;
        color: white;
        padding: 5px 10px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        margin-top: 5px;
      }
      .copy-btn:hover {
        background-color: #45a049;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <h2>로컬 Wi-Fi 정보 입력</h2>
      <p class="hint">SSID와 Password를 입력하면 ESP32가 해당 Wi-Fi에 연결합니다.</p>
      <div class="board-id">
        <strong>보드 ID:</strong><br>
        <span id="boardId">)rawliteral" + currentBoardId + R"rawliteral(</span>
        <button class="copy-btn" onclick="copyBoardId()">복사하기</button>
        <p class="hint">이 보드 ID를 서버에 등록할 때 사용하세요.</p>
      </div>
      <form action="/submit" method="POST">
        <label>SSID:</label><br>
        <input type="text" name="ssid" placeholder="예: MyHomeWiFi" required><br>
        <label>Password:</label><br>
        <input type="password" name="password" placeholder="Wi-Fi 비밀번호" required><br>
        <button type="submit">저장 후 연결</button>
      </form>
    </div>
    <script>
      function copyBoardId() {
        const boardId = document.getElementById('boardId').textContent;
        navigator.clipboard.writeText(boardId).then(() => {
          alert('보드 ID가 복사되었습니다!');
        }).catch(err => {
          console.error('복사 실패:', err);
        });
      }
    </script>
  </body>
</html>
  )rawliteral";

  webServer.send(200, "text/html; charset=UTF-8", html);
}

// === 폼 제출(POST) 처리 ===
void handleFormSubmit() {
  if (webServer.hasArg("ssid") && webServer.hasArg("password")) {
    formSSID     = webServer.arg("ssid");
    formPassword = webServer.arg("password");
    hasWiFiConfig = true;

    // 응답 페이지
    String response = R"rawliteral(
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Wi-Fi 연결중...</title>
  </head>
  <body>
    <h3>입력하신 Wi-Fi로 연결을 시도합니다…</h3>
    <p>잠시 기다려주신 후 전원을 다시 켜주세요.</p>
    <p>보드 ID: )rawliteral" + boardId + R"rawliteral(</p>
  </body>
</html>
    )rawliteral";
    webServer.send(200, "text/html; charset=UTF-8", response);

    Serial.printf("▷ 폼에서 입력된 SSID: %s, PW: %s\n", formSSID.c_str(), formPassword.c_str());

    // NVS에 저장
    preferences.begin("wifi", false);
    preferences.putString("ssid", formSSID);
    preferences.putString("pw", formPassword);
    preferences.end();

    // AP 모드 종료 후 STA 모드로 전환하여 연결 시도
    Serial.println("▶ AP 모드 종료, STA 모드로 전환");
    WiFi.softAPdisconnect(true);  // AP 종료
    delay(100);

    tryConnectSavedWiFi();

    // 연결 성공 시 MQTT 연결
    if (WiFi.status() == WL_CONNECTED) {
      connectAWS();
    } else {
      Serial.println("❌ 입력된 Wi-Fi 연결 실패");
      Serial.println("▶ ESP32 재부팅 후 다시 시도해주세요.");
    }
  } else {
    webServer.send(400, "text/plain", "잘못된 요청: SSID 또는 PW 누락");
  }
}

// === 404 처리 ===
void handleNotFound() {
  webServer.send(404, "text/plain", "404: 페이지를 찾을 수 없습니다.");
}

// === Wi-Fi 연결 후 MQTT 연결 (AP/NVS 통합) ===
void connectToMQTT() {
  net.setCACert(AWS_ROOT_CA);
  net.setCertificate(DEVICE_CERT);
  net.setPrivateKey(DEVICE_KEY);

  client.setServer(mqttServer, mqttPort);
  client.setCallback(mqttCallback);

  Serial.print("🔐 MQTT 연결 시도...");
  while (!client.connect("ESP32FishTank")) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("\n✅ MQTT 연결 성공!");

  // 토픽 구독 (원래 코드와 동일)
  String feedTopic   = String("aquatrack/") + boardId + "/feeding";
  String coolerTopic = String("aquatrack/") + boardId + "/cooler";
  String pumpTopic   = String("aquatrack/") + boardId + "/pump";

  client.subscribe(feedTopic.c_str(),   1);
  client.subscribe(coolerTopic.c_str(), 1);
  client.subscribe(pumpTopic.c_str(),   1);

  Serial.println("구독 완료: " + feedTopic);
  Serial.println("구독 완료: " + coolerTopic);
  Serial.println("구독 완료: " + pumpTopic);
}
