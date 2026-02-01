package com.airport.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Booking entity - represents a flight booking/reservation.
 * Demonstrates composition with User, Flight, and Passenger relationships.
 * Adapted from original Booking.java for JPA persistence.
 */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    private LocalDateTime bookingDate;

    private String seatNumber;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.CONFIRMED;

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    // Default constructor
    public Booking() {
        this.bookingDate = LocalDateTime.now();
    }

    // Parameterized constructor
    public Booking(User user, Flight flight, Passenger passenger, String seatNumber) {
        this.user = user;
        this.flight = flight;
        this.passenger = passenger;
        this.seatNumber = seatNumber;
        this.bookingDate = LocalDateTime.now();
        this.bookingReference = generateBookingReference();
        calculateTotalPrice();
    }

    /**
     * Generate a unique booking reference.
     */
    private String generateBookingReference() {
        return "TXR" + System.currentTimeMillis() % 1000000;
    }

    /**
     * Calculate total price with customer discount.
     * Preserves polymorphic discount calculation from original design.
     */
    public void calculateTotalPrice() {
        if (flight != null && user != null) {
            double basePrice = flight.getBasePrice();
            double discount = user.calculateDiscount();
            this.totalPrice = basePrice - (basePrice * discount);
        }
    }

    /**
     * Confirm the booking by reserving a seat on the flight.
     */
    public boolean confirm() {
        if (flight != null && flight.bookSeat()) {
            this.status = BookingStatus.CONFIRMED;
            return true;
        }
        return false;
    }

    /**
     * Cancel the booking and release the seat.
     */
    public void cancel() {
        if (flight != null && status == BookingStatus.CONFIRMED) {
            flight.cancelSeat();
            this.status = BookingStatus.CANCELLED;
        }
    }

    /**
     * Get discount amount applied to this booking.
     */
    public double getDiscountAmount() {
        if (flight != null && user != null) {
            return flight.getBasePrice() * user.calculateDiscount();
        }
        return 0.0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingReference='" + bookingReference + '\'' +
                ", flight=" + (flight != null ? flight.getFlightNumber() : "N/A") +
                ", passenger=" + (passenger != null ? passenger.getFullName() : "N/A") +
                ", seatNumber='" + seatNumber + '\'' +
                ", totalPrice=$" + String.format("%.2f", totalPrice) +
                ", status=" + status +
                '}';
    }
}
