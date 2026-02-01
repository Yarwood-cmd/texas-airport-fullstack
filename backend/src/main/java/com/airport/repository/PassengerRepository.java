package com.airport.repository;

import com.airport.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    List<Passenger> findByUserId(Long userId);
    
    List<Passenger> findByLastNameIgnoreCase(String lastName);
}
