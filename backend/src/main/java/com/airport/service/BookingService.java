package com.airport.service;

import com.airport.model.Booking;
import com.airport.model.Flight;
import com.airport.model.Passenger;
import com.airport.model.User;
import com.airport.repository.BookingRepository;
import com.airport.repository.FlightRepository;
import com.airport.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Booking service - business logic for booking operations.
 * Replaces Airport class booking management methods.
 */
@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          FlightRepository flightRepository,
                          PassengerRepository passengerRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference);
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getActiveBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdAndStatus(userId, Booking.BookingStatus.CONFIRMED);
    }

    /**
     * Create a new booking - core booking logic.
     */
    public Booking createBooking(User user, Long flightId, Passenger passenger, String seatNumber) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (!flight.hasAvailableSeats()) {
            throw new RuntimeException("No available seats on this flight");
        }

        // Associate passenger with user if not already
        if (passenger.getUser() == null) {
            passenger.setUser(user);
        }
        passenger = passengerRepository.save(passenger);

        // Create booking
        Booking booking = new Booking(user, flight, passenger, seatNumber);
        
        // Confirm booking (reserves seat)
        if (!booking.confirm()) {
            throw new RuntimeException("Failed to confirm booking - no seats available");
        }

        // Save flight with updated seat count
        flightRepository.save(flight);

        // Add miles for frequent flyers
        if (user.getCustomerType() == User.CustomerType.FREQUENT_FLYER) {
            // Estimate miles based on a simple calculation (could be enhanced)
            user.addMiles(500);
        }

        return bookingRepository.save(booking);
    }

    /**
     * Cancel an existing booking.
     */
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.cancel();
        
        // Save flight with updated seat count
        flightRepository.save(booking.getFlight());

        return bookingRepository.save(booking);
    }

    /**
     * Cancel booking by reference number.
     */
    public Booking cancelBookingByReference(String reference) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return cancelBooking(booking.getId());
    }

    /**
     * Get booking statistics for a user.
     */
    public BookingStats getBookingStats(Long userId) {
        List<Booking> userBookings = bookingRepository.findByUserId(userId);
        
        long totalBookings = userBookings.size();
        long confirmedBookings = userBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();
        long cancelledBookings = userBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED)
                .count();
        double totalSpent = userBookings.stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        return new BookingStats(totalBookings, confirmedBookings, cancelledBookings, totalSpent);
    }

    /**
     * Simple DTO for booking statistics.
     */
    public record BookingStats(
            long totalBookings,
            long confirmedBookings,
            long cancelledBookings,
            double totalSpent
    ) {}
}
