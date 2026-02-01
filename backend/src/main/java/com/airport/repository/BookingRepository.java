package com.airport.repository;

import com.airport.model.Booking;
import com.airport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByUser(User user);
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    List<Booking> findByUserIdAndStatus(Long userId, Booking.BookingStatus status);
}
