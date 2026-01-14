package com.geety.grabmyseat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.geety.grabmyseat.model.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long>{
@Query("""
    SELECT s FROM Seat s
    WHERE s.status = 'HELD'
    AND s.holdUntil < :now
""")
List<Seat> findExpiredHolds(LocalDateTime now);

    
} 
