package com.airport.service;

import com.airport.model.Flight;
import com.airport.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Flight service - business logic for flight operations.
 * Replaces Airport class flight management methods.
 */
@Service
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Flight> getAvailableFlights() {
        return flightRepository.findAvailableFlights();
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public Optional<Flight> getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    public List<Flight> searchByDestination(String destination) {
        return flightRepository.findByDestinationIgnoreCase(destination);
    }

    public List<Flight> searchByOrigin(String origin) {
        return flightRepository.findByOriginIgnoreCase(origin);
    }

    public List<Flight> searchByRoute(String origin, String destination) {
        return flightRepository.findByRoute(origin, destination);
    }

    public List<Flight> searchAvailableByRoute(String origin, String destination) {
        return flightRepository.findAvailableByRoute(origin, destination);
    }

    public Flight createFlight(Flight flight) {
        // Ensure available seats equals capacity for new flights
        if (flight.getAvailableSeats() == 0) {
            flight.setAvailableSeats(flight.getCapacity());
        }
        return flightRepository.save(flight);
    }

    public Flight updateFlight(Long id, Flight flightDetails) {
        return flightRepository.findById(id)
                .map(flight -> {
                    flight.setFlightNumber(flightDetails.getFlightNumber());
                    flight.setOrigin(flightDetails.getOrigin());
                    flight.setDestination(flightDetails.getDestination());
                    flight.setDepartureTime(flightDetails.getDepartureTime());
                    flight.setCapacity(flightDetails.getCapacity());
                    flight.setBasePrice(flightDetails.getBasePrice());
                    return flightRepository.save(flight);
                })
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public boolean bookSeat(Long flightId) {
        return flightRepository.findById(flightId)
                .map(flight -> {
                    boolean booked = flight.bookSeat();
                    if (booked) {
                        flightRepository.save(flight);
                    }
                    return booked;
                })
                .orElse(false);
    }

    public void cancelSeat(Long flightId) {
        flightRepository.findById(flightId)
                .ifPresent(flight -> {
                    flight.cancelSeat();
                    flightRepository.save(flight);
                });
    }
}
