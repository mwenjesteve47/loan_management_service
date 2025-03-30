package com.example.loanmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoanManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanManagementServiceApplication.class, args);
    }

}
