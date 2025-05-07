package com.aquatrack;

import com.aquatrack.user.entity.User;
import com.aquatrack.user.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*@SpringBootApplication
@EntityScan(basePackages = {
        "com.aquatrack.user.entity",
        "com.aquatrack.aquarium.entity",
        "com.aquatrack.feeding.entity",
        "com.aquatrack.notification.entity",
        "com.aquatrack.alert.entity",
        "com.aquatrack.sensor.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.aquatrack.user.repository",
        "com.aquatrack.aquarium.repository",
        "com.aquatrack.feeding.repository",
        "com.aquatrack.notification.repository",
        "com.aquatrack.alert.repository",
        "com.aquatrack.sensor.repository"
})*/

@SpringBootApplication(scanBasePackages = "com.aquatrack")
@EntityScan("com.aquatrack")  // 이거 하나면 모든 하위 엔티티 잡힘
@EnableJpaRepositories("com.aquatrack") // 모든 하위 리포지토리 자동 포함

public class AquatrackBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AquatrackBackendApplication.class, args);
    }

}
