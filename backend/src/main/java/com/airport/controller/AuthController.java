package com.airport.controller;

import com.airport.model.User;
import com.airport.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller - handles user registration and login.
 * 
 * Endpoints:
 * POST /api/auth/register     - Register new user
 * POST /api/auth/login        - Login and get JWT token
 * POST /api/auth/register/ff  - Register as frequent flyer
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new regular user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(
                    request.name(),
                    request.email(),
                    request.password(),
                    request.phoneNumber()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Register a new frequent flyer.
     */
    @PostMapping("/register/frequent-flyer")
    public ResponseEntity<?> registerFrequentFlyer(@Valid @RequestBody FrequentFlyerRegisterRequest request) {
        try {
            User user = authService.registerFrequentFlyer(
                    request.name(),
                    request.email(),
                    request.password(),
                    request.phoneNumber(),
                    request.initialMiles()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Login and get JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthService.AuthResponse response = authService.login(request.email(), request.password());
            return ResponseEntity.ok(new LoginResponse(
                    response.token(),
                    new UserResponse(response.user())
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Request/Response DTOs
    
    public record RegisterRequest(
            @NotBlank(message = "Name is required") String name,
            @NotBlank(message = "Email is required") @Email String email,
            @NotBlank(message = "Password is required") String password,
            String phoneNumber
    ) {}

    public record FrequentFlyerRegisterRequest(
            @NotBlank(message = "Name is required") String name,
            @NotBlank(message = "Email is required") @Email String email,
            @NotBlank(message = "Password is required") String password,
            String phoneNumber,
            int initialMiles
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Email is required") @Email String email,
            @NotBlank(message = "Password is required") String password
    ) {}

    public record LoginResponse(String token, UserResponse user) {}

    public record UserResponse(
            Long id,
            String name,
            String email,
            String phoneNumber,
            String customerType,
            String membershipLevel,
            int milesFlown,
            int discountPercent
    ) {
        public UserResponse(User user) {
            this(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getCustomerType().name(),
                    user.getMembershipLevel().name(),
                    user.getMilesFlown(),
                    user.getDiscountPercent()
            );
        }
    }

    public record ErrorResponse(String message) {}
}
