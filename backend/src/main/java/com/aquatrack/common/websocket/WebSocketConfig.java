package com.aquatrack.common.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 연결할 웹소켓 엔드포인트
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") // 개발 단계에서는 허용
                .withSockJS(); // SockJS fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독용 메시지 브로커 경로
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app"); // 메시지 발송 prefix (선택 사항)
    }
}
