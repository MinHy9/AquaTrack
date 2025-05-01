package com.example.aquarium.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;


@Service
public class SmsService {
	
	 @Value("${solapi.apiKey}")//키 설정후 자신의 맞는 키로 application.properties파일에 작성
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
	            //message.setFrom(); //인증된 발신번호로 바꾸기
	            message.setTo(phoneNumber);
	            message.setText(messageText);

	            messageService.send(message);
	        } catch (Exception e) {
	            throw new RuntimeException("SMS 전송 실패: " + e.getMessage(), e);
	        }
	 } 

}
