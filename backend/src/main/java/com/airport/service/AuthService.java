package com.airport.service;

import com.airport.model.User;
import com.airport.repository.UserRepository;
import com.airport.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Authentication service - handles user registration and login.
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register a new user.
     */
    public User register(String name, String email, String password, String phoneNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(name, email, passwordEncoder.encode(password), phoneNumber);
        return userRepository.save(user);
    }

    /**
     * Register a new frequent flyer.
     */
    public User registerFrequentFlyer(String name, String email, String password, 
                                       String phoneNumber, int initialMiles) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(name, email, passwordEncoder.encode(password), phoneNumber, initialMiles);
        return userRepository.save(user);
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user);
    }

    /**
     * Get user by email.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Update user profile.
     */
    public User updateProfile(Long userId, String name, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        return userRepository.save(user);
    }

    /**
     * Upgrade user to frequent flyer.
     */
    public User upgradeToFrequentFlyer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.upgradeToFrequentFlyer();
        return userRepository.save(user);
    }

    /**
     * Authentication response DTO.
     */
    public record AuthResponse(String token, User user) {}
}
