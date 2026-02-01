package com.airport.controller;

import com.airport.model.Booking;
import com.airport.model.Passenger;
import com.airport.model.User;
import com.airport.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Booking REST Controller - handles booking-related HTTP requests.
 * 
 * Endpoints:
 * GET    /api/bookings           - Get user's bookings
 * GET    /api/bookings/{id}      - Get booking by ID
 * POST   /api/bookings           - Create new booking
 * DELETE /api/bookings/{id}      - Cancel booking
 * GET    /api/bookings/stats     - Get booking statistics
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Get all bookings for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal User user) {
        List<Booking> bookings = bookingService.getBookingsByUser(user.getId());
        List<BookingResponse> responses = bookings.stream()
                .map(BookingResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get active bookings for the authenticated user.
     */
    @GetMapping("/active")
    public ResponseEntity<List<BookingResponse>> getActiveBookings(@AuthenticationPrincipal User user) {
        List<Booking> bookings = bookingService.getActiveBookingsByUser(user.getId());
        List<BookingResponse> responses = bookings.stream()
                .map(BookingResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get booking by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return bookingService.getBookingById(id)
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .map(booking -> ResponseEntity.ok(new BookingResponse(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get booking by reference number.
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<BookingResponse> getBookingByReference(
            @PathVariable String reference,
            @AuthenticationPrincipal User user) {
        return bookingService.getBookingByReference(reference)
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .map(booking -> ResponseEntity.ok(new BookingResponse(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new booking.
     */
    @PostMapping
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User user) {
        try {
            Passenger passenger = new Passenger(
                    request.passengerFirstName(),
                    request.passengerLastName(),
                    request.passengerAge(),
                    Passenger.SeatPreference.valueOf(request.seatPreference().toUpperCase())
            );

            Booking booking = bookingService.createBooking(
                    user,
                    request.flightId(),
                    passenger,
                    request.seatNumber()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(new BookingResponse(booking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cancel a booking.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            // Verify booking belongs to user
            Booking booking = bookingService.getBookingById(id)
                    .filter(b -> b.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            Booking cancelled = bookingService.cancelBooking(id);
            return ResponseEntity.ok(new BookingResponse(cancelled));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cancel booking by reference.
     */
    @DeleteMapping("/reference/{reference}")
    public ResponseEntity<?> cancelBookingByReference(
            @PathVariable String reference,
            @AuthenticationPrincipal User user) {
        try {
            // Verify booking belongs to user
            bookingService.getBookingByReference(reference)
                    .filter(b -> b.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            Booking cancelled = bookingService.cancelBookingByReference(reference);
            return ResponseEntity.ok(new BookingResponse(cancelled));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get booking statistics for the authenticated user.
     */
    @GetMapping("/stats")
    public ResponseEntity<BookingService.BookingStats> getBookingStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getBookingStats(user.getId()));
    }

    // Request/Response DTOs

    public record BookingRequest(
            @NotNull(message = "Flight ID is required") Long flightId,
            @NotBlank(message = "Passenger first name is required") String passengerFirstName,
            @NotBlank(message = "Passenger last name is required") String passengerLastName,
            int passengerAge,
            String seatPreference,
            @NotBlank(message = "Seat number is required") String seatNumber
    ) {}

    public record BookingResponse(
            Long id,
            String bookingReference,
            String flightNumber,
            String origin,
            String destination,
            String departureTime,
            String passengerName,
            String seatNumber,
            double totalPrice,
            double discountAmount,
            String status,
            String bookingDate
    ) {
        public BookingResponse(Booking booking) {
            this(
                    booking.getId(),
                    booking.getBookingReference(),
                    booking.getFlight().getFlightNumber(),
                    booking.getFlight().getOrigin(),
                    booking.getFlight().getDestination(),
                    booking.getFlight().getDepartureTime(),
                    booking.getPassenger().getFullName(),
                    booking.getSeatNumber(),
                    booking.getTotalPrice(),
                    booking.getDiscountAmount(),
                    booking.getStatus().name(),
                    booking.getBookingDate().toString()
            );
        }
    }

    public record ErrorResponse(String message) {}
}
