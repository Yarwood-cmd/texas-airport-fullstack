package com.airport.controller;

import com.airport.model.Flight;
import com.airport.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Flight REST Controller - handles flight-related HTTP requests.
 * 
 * Endpoints:
 * GET    /api/flights          - List all flights
 * GET    /api/flights/{id}     - Get flight by ID
 * GET    /api/flights/search   - Search flights
 * POST   /api/flights          - Create new flight (Admin)
 * PUT    /api/flights/{id}     - Update flight (Admin)
 * DELETE /api/flights/{id}     - Delete flight (Admin)
 */
@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Get all flights.
     */
    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    /**
     * Get all available flights (with seats).
     */
    @GetMapping("/available")
    public ResponseEntity<List<Flight>> getAvailableFlights() {
        return ResponseEntity.ok(flightService.getAvailableFlights());
    }

    /**
     * Get flight by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get flight by flight number.
     */
    @GetMapping("/number/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        return flightService.getFlightByNumber(flightNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search flights by destination.
     */
    @GetMapping("/search/destination/{destination}")
    public ResponseEntity<List<Flight>> searchByDestination(@PathVariable String destination) {
        return ResponseEntity.ok(flightService.searchByDestination(destination));
    }

    /**
     * Search flights by origin.
     */
    @GetMapping("/search/origin/{origin}")
    public ResponseEntity<List<Flight>> searchByOrigin(@PathVariable String origin) {
        return ResponseEntity.ok(flightService.searchByOrigin(origin));
    }

    /**
     * Search flights by route (origin and destination).
     */
    @GetMapping("/search/route")
    public ResponseEntity<List<Flight>> searchByRoute(
            @RequestParam String origin,
            @RequestParam String destination) {
        return ResponseEntity.ok(flightService.searchByRoute(origin, destination));
    }

    /**
     * Search available flights by route.
     */
    @GetMapping("/search/available")
    public ResponseEntity<List<Flight>> searchAvailableByRoute(
            @RequestParam String origin,
            @RequestParam String destination) {
        return ResponseEntity.ok(flightService.searchAvailableByRoute(origin, destination));
    }

    /**
     * Create a new flight (Admin only).
     */
    @PostMapping
    public ResponseEntity<Flight> createFlight(@Valid @RequestBody Flight flight) {
        Flight created = flightService.createFlight(flight);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing flight (Admin only).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody Flight flight) {
        try {
            Flight updated = flightService.updateFlight(id, flight);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a flight (Admin only).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
