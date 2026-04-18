package com.medischeduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MediSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediSchedulerApplication.class, args);
    }

}
