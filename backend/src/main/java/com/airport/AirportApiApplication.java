package com.airport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Texas Regional Airport Reservation System - REST API
 * 
 * Spring Boot backend providing RESTful endpoints for:
 * - User authentication (JWT)
 * - Flight management
 * - Booking operations
 * 
 * @author Paul Yarwood
 */
@SpringBootApplication
public class AirportApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirportApiApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  TEXAS AIRPORT RESERVATION API");
        System.out.println("  Server running on http://localhost:8080");
        System.out.println("========================================\n");
    }
}
