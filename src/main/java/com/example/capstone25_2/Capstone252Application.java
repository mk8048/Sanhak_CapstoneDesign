package com.example.capstone25_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// ⭐️ 이 import 추가
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
public class Capstone252Application {

    public static void main(String[] args) {
        // ⭐️ WebApplicationType을 명시적으로 설정
        SpringApplication application = new SpringApplication(Capstone252Application.class);
        application.setWebApplicationType(WebApplicationType.SERVLET); // ⭐️ SERVLET (웹)으로 설정
        application.run(args);
    }
}
