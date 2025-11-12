package com.andina.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MicroservicioAccionesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroservicioAccionesApplication.class, args);
    }
}
