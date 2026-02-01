package com.airport.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * User entity - represents a user in the airport system.
 * Combines Customer authentication with FrequentFlyer/RegularCustomer logic.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType = CustomerType.REGULAR;

    // Frequent flyer specific fields
    private int milesFlown = 0;

    @Enumerated(EnumType.STRING)
    private MembershipLevel membershipLevel = MembershipLevel.NONE;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // Enums
    public enum CustomerType {
        REGULAR, FREQUENT_FLYER
    }

    public enum MembershipLevel {
        NONE, SILVER, GOLD, PLATINUM
    }

    public enum Role {
        USER, ADMIN
    }

    // Default constructor
    public User() {
    }

    // Constructor for regular user
    public User(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.customerType = CustomerType.REGULAR;
        this.membershipLevel = MembershipLevel.NONE;
    }

    // Constructor for frequent flyer
    public User(String name, String email, String password, String phoneNumber,
                int milesFlown) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.customerType = CustomerType.FREQUENT_FLYER;
        this.milesFlown = milesFlown;
        updateMembershipLevel();
    }

    /**
     * Calculate discount based on customer type and membership level.
     * Preserves polymorphic behavior from original design.
     */
    public double calculateDiscount() {
        if (customerType == CustomerType.REGULAR) {
            return 0.0;
        }
        
        // Frequent flyer discounts
        return switch (membershipLevel) {
            case PLATINUM -> 0.20;
            case GOLD -> 0.15;
            case SILVER -> 0.10;
            default -> 0.0;
        };
    }

    /**
     * Add miles and update membership level accordingly.
     */
    public void addMiles(int miles) {
        this.milesFlown += miles;
        if (customerType == CustomerType.FREQUENT_FLYER) {
            updateMembershipLevel();
        }
    }

    /**
     * Update membership level based on miles flown.
     */
    private void updateMembershipLevel() {
        if (milesFlown >= 50000) {
            membershipLevel = MembershipLevel.PLATINUM;
        } else if (milesFlown >= 25000) {
            membershipLevel = MembershipLevel.GOLD;
        } else if (milesFlown > 0) {
            membershipLevel = MembershipLevel.SILVER;
        }
    }

    /**
     * Upgrade user to frequent flyer status.
     */
    public void upgradeToFrequentFlyer() {
        this.customerType = CustomerType.FREQUENT_FLYER;
        this.membershipLevel = MembershipLevel.SILVER;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public int getMilesFlown() {
        return milesFlown;
    }

    public void setMilesFlown(int milesFlown) {
        this.milesFlown = milesFlown;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(MembershipLevel membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getDiscountPercent() {
        return (int) (calculateDiscount() * 100);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", customerType=" + customerType +
                ", membershipLevel=" + membershipLevel +
                ", discount=" + getDiscountPercent() + "%" +
                '}';
    }
}
