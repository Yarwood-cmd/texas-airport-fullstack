package com.airport.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Passenger entity - represents a passenger on a flight.
 * Adapted from original Passenger.java for JPA persistence.
 */
@Entity
@Table(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Min(value = 0, message = "Age must be positive")
    private int age;

    @Enumerated(EnumType.STRING)
    private SeatPreference seatPreference = SeatPreference.NO_PREFERENCE;

    // Associated user (optional - for logged-in bookings)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public enum SeatPreference {
        WINDOW, AISLE, MIDDLE, NO_PREFERENCE
    }

    // Default constructor
    public Passenger() {
    }

    // Parameterized constructor
    public Passenger(String firstName, String lastName, int age, SeatPreference seatPreference) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.seatPreference = seatPreference;
    }

    // Convenience method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Check if passenger is a minor
    public boolean isMinor() {
        return age < 18;
    }

    // Check if passenger is a senior
    public boolean isSenior() {
        return age >= 65;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public SeatPreference getSeatPreference() {
        return seatPreference;
    }

    public void setSeatPreference(SeatPreference seatPreference) {
        this.seatPreference = seatPreference;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", name='" + getFullName() + '\'' +
                ", age=" + age +
                ", seatPreference=" + seatPreference +
                '}';
    }
}
