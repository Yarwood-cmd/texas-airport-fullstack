package com.airport.config;

import com.airport.model.Flight;
import com.airport.model.User;
import com.airport.repository.FlightRepository;
import com.airport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer - seeds the database with sample Texas flights.
 * Mirrors the initializeSampleData() method from original AirportSystem.java
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Initialize sample flights (Texas routes - same as original)
        if (flightRepository.count() == 0) {
            System.out.println("Initializing sample flight data...\n");

            flightRepository.save(new Flight("TX101", "Dallas", "Austin", "08:00 AM", 150, 199.99));
            flightRepository.save(new Flight("TX102", "Houston", "San Antonio", "10:30 AM", 120, 149.99));
            flightRepository.save(new Flight("TX103", "Austin", "Dallas", "02:00 PM", 150, 199.99));
            flightRepository.save(new Flight("TX104", "El Paso", "Lubbock", "09:15 AM", 80, 129.99));
            flightRepository.save(new Flight("TX105", "Corpus Christi", "Amarillo", "11:45 AM", 100, 179.99));
            
            // Additional flights for more variety
            flightRepository.save(new Flight("TX106", "Dallas", "Houston", "07:00 AM", 180, 159.99));
            flightRepository.save(new Flight("TX107", "San Antonio", "Austin", "09:00 AM", 100, 89.99));
            flightRepository.save(new Flight("TX108", "Houston", "Dallas", "03:30 PM", 180, 159.99));
            flightRepository.save(new Flight("TX109", "Austin", "El Paso", "12:00 PM", 120, 229.99));
            flightRepository.save(new Flight("TX110", "Lubbock", "Dallas", "04:00 PM", 80, 149.99));

            System.out.println("Sample flights initialized!\n");
        }

        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@texasairport.com")) {
            User admin = new User("Admin", "admin@texasairport.com", 
                    passwordEncoder.encode("admin123"), "555-0000");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created: admin@texasairport.com / admin123");
        }

        // Create sample regular user
        if (!userRepository.existsByEmail("john@example.com")) {
            User regularUser = new User("John Doe", "john@example.com",
                    passwordEncoder.encode("password123"), "555-1234");
            userRepository.save(regularUser);
            System.out.println("Sample user created: john@example.com / password123");
        }

        // Create sample frequent flyer
        if (!userRepository.existsByEmail("jane@example.com")) {
            User frequentFlyer = new User("Jane Smith", "jane@example.com",
                    passwordEncoder.encode("password123"), "555-5678", 30000);
            userRepository.save(frequentFlyer);
            System.out.println("Sample frequent flyer created: jane@example.com / password123 (Gold - 15% discount)");
        }

        System.out.println("\n========================================");
        System.out.println("  API Endpoints Ready:");
        System.out.println("  POST /api/auth/register");
        System.out.println("  POST /api/auth/login");
        System.out.println("  GET  /api/flights");
        System.out.println("  GET  /api/flights/available");
        System.out.println("  POST /api/bookings");
        System.out.println("  GET  /api/bookings");
        System.out.println("========================================\n");
    }
}
