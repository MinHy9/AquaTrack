package com.aquatrack.common.mqtt;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.cert.ca}")
    private String caCertPath;

    @Value("${mqtt.cert.crt}")
    private String clientCertPath;

    @Value("${mqtt.cert.key}")
    private String privateKeyPath;

    @Bean
    public MqttClient mqttClient() throws Exception {
        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setSocketFactory(AwsIotMqttUtil.getSocketFactory(
                caCertPath,
                clientCertPath,
                privateKeyPath
        ));

        client.connect(options);

        // 구독 설정
       /* client.subscribe("aquatrack/+/sensor", (topic, msg) -> {
            String payload = new String(msg.getPayload());
            
            System.out.println("💡 수신됨: [" + topic + "] " + payload);
        });*/

        return client;
    }
}
