package com.project.classOfferingBookingSystem.repository;

import com.project.classOfferingBookingSystem.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    @Query("""
        SELECT COUNT(s) > 0 FROM Booking b
        JOIN b.sessions s
        WHERE b.parent.id = :userId
        AND s.startTime < :endTime
        AND s.endTime   > :startTime
        """)
    boolean checkForConflict(@Param("userId")UUID userId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
}
