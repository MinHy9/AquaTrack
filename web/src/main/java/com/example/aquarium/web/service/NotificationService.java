package com.example.aquarium.web.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.aquarium.web.entity.Alert;
import com.example.aquarium.web.entity.Notification;
import com.example.aquarium.web.entity.User;
import com.example.aquarium.web.repository.AlertRepository;
import com.example.aquarium.web.repository.NotificationRespository;
import com.example.aquarium.web.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Service
public class NotificationService {
	
	 @Value("${solapi.apiKey}")//키 설정후 자신의 맞는 키로 application.properties파일에 작성
	 private String apiKey;
	 @Value("${solapi.apiSecret}")
	 private String secretKey;
	
	private final UserRepository userRepository;
	private DefaultMessageService messageService;
	private AlertRepository alertRepository;
	private NotificationRespository notificationRespository;
	
	public NotificationService(UserRepository userRepository,AlertRepository alertRepository,NotificationRespository notificationRespository) {
		super();
		this.userRepository = userRepository;
		this.alertRepository = alertRepository;
		this.notificationRespository = notificationRespository;
	}
	 
	@PostConstruct
	 private void init() {
	     messageService = NurigoApp.INSTANCE.initialize(apiKey, secretKey, "https://api.solapi.com");
	 }
	@Transactional
	public void createTestAlert(int userid) {
		User user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));
		Alert alert = new Alert("물의 온도가 높습니다.",false,LocalDateTime.now(),Alert.AlertType.TEMPARATURE_WARNING);
		user.addAlert(alert);
		
		alertRepository.save(alert);
		Notification notification = new Notification(user.getUsername(),alert.getCreatedAt(),"물 운도와 관련된 얼람");
		
		alert.setNotification(notification);
		notification.setUser(user);
		notification.setAlert(alert);
		notificationRespository.save(notification);		
	}
	
	public void sendMessage(int userid) {
		User user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));
		List<Notification> notiftificationList = user.getNotifications();
		//notiftificationList.stream();
		for(Notification notification : notiftificationList) {
			Message message = new Message();
			message.setFrom("01012345678");//solapi에 등록한 번호로 설정
			message.setTo(user.getPhoneNumber());
			message.setText(notification.getAlert().getMessage());
			try {
				messageService.send(message);
				System.out.println(user.getPhoneNumber());
			}
			catch (NurigoMessageNotReceivedException exception) {
				 // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
				 System.out.println(exception.getFailedMessageList());
				 System.out.println(exception.getMessage());
			}catch (Exception exception) {
				 System.out.println(exception.getMessage());
			}
		}		
	}

}
