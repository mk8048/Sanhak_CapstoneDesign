package com.example.capstone25_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// ⭐️ 이 import 추가
import org.springframework.boot.WebApplicationType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync    // 비동기 사용
@EnableScheduling //스케줄링 활성화
@SpringBootApplication
public class Capstone252Application {

    public static void main(String[] args) {
        // ⭐️ WebApplicationType을 명시적으로 설정
        SpringApplication application = new SpringApplication(Capstone252Application.class);
        application.setWebApplicationType(WebApplicationType.SERVLET); // ⭐️ SERVLET (웹)으로 설정
        application.run(args);
    }
}

