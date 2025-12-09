package com.coopcredit.creditapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Credit Application Service.
 * This microservice handles credit application management following hexagonal
 * architecture.
 */
@SpringBootApplication
public class CreditApplicationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditApplicationServiceApplication.class, args);
    }
}
