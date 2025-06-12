package com.aquatrack.notification.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.model.Message;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${solapi.apiKey}")
    private String apiKey;

    @Value("${solapi.apiSecret}")
    private String secretKey;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        messageService = NurigoApp.INSTANCE.initialize(apiKey, secretKey, "https://api.solapi.com");
    }

    public void sendSms(String phoneNumber, String messageText) {
        try {
            Message message = new Message();
            message.setFrom("01094744116"); // ✅ 본인 인증된 발신번호로 교체
            message.setTo(phoneNumber);
            message.setText(messageText);

            messageService.send(message);
        } catch (Exception e) {
            throw new RuntimeException("SMS 전송 실패: " + e.getMessage(), e);
        }
    }
}
