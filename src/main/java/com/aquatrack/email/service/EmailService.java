package com.aquatrack.email.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {
	
	private JavaMailSender mailSender;
	public void sendTempPassword(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("임시 비밀번호 안내");
        message.setText("임시 비밀번호는: " + tempPassword + " 입니다.\n로그인 후 꼭 변경하세요.");
        message.setFrom("your_email@gmail.com");
        mailSender.send(message);
	 }
	
}
