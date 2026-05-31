package com.project.classOfferingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name="bookings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "offering_id"})
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id")
    private Offering offering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id")
    private User parent;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "booking_sessions",
        joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    private List<Session> sessions;

    @Builder.Default
    @Column(name = "booked_at", nullable = false)
    private Instant bookedAt = Instant.now();
}
