package com.project.classOfferingBookingSystem.repository;

import com.project.classOfferingBookingSystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("select u from Booking u join fetch u.offering left join fetch u.sessions where u.parent.id = :parentId")
    List<Booking> getBookingsByParentId(@Param("parentId") UUID parentId);

    boolean existsByParentIdAndOfferingId(UUID parentId, UUID offeringId);
}
