package com.airport.repository;

import com.airport.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    List<Flight> findByDestinationIgnoreCase(String destination);
    
    List<Flight> findByOriginIgnoreCase(String origin);
    
    @Query("SELECT f FROM Flight f WHERE f.availableSeats > 0")
    List<Flight> findAvailableFlights();
    
    @Query("SELECT f FROM Flight f WHERE LOWER(f.origin) = LOWER(?1) AND LOWER(f.destination) = LOWER(?2)")
    List<Flight> findByRoute(String origin, String destination);
    
    @Query("SELECT f FROM Flight f WHERE LOWER(f.origin) = LOWER(?1) AND LOWER(f.destination) = LOWER(?2) AND f.availableSeats > 0")
    List<Flight> findAvailableByRoute(String origin, String destination);
}
