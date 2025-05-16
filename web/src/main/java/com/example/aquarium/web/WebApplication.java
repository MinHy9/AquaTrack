package com.example.aquarium.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
	//이건 프런트랑 연동하기 위해 필요
    @Configuration 
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            // CORS 설정
            registry.addMapping("/**") // 모든 엔드포인트에 대해 CORS 적용
                    .allowedOrigins("*") // 모든 출처 허용
                    .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메소드 설정
                    .allowedHeaders("*") // 모든 헤더 허용
                    .allowCredentials(true); // 쿠키와 인증 정보 전송 허용
        }
    }

}
