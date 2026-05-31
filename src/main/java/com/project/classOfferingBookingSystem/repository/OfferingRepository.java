package com.project.classOfferingBookingSystem.repository;

import com.project.classOfferingBookingSystem.entity.Offering;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferingRepository extends JpaRepository<Offering, UUID> {

    @Query("select u from Offering u join fetch u.teacher join fetch u.sessions where u.teacher.id = :teacherId")
    List<Offering> getOfferingByTeacher(@Param("teacherId") UUID teacherId);

    @Query("select u from Offering u join fetch u.teacher left join fetch u.sessions")
    List<Offering> findAllWithSessions();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Offering u where u.id = :id")
    Optional<Offering> findByIdWithLock(@Param("id") UUID id);
}
