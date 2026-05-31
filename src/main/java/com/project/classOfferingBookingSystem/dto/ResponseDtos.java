package com.project.classOfferingBookingSystem.dto;

import com.project.classOfferingBookingSystem.entity.Session;

import java.util.List;
import java.util.UUID;

public final class ResponseDtos {
    private ResponseDtos(){};

    public record OfferingResponse(
            UUID id,
            String title,
            String description,
            String teacher,
            List<SessionResponse> sessions
    ){}

    public record SessionResponse(
            UUID id,
            String startTime,
            String endTime
    ){}

    public record BookingResponse(
            UUID bookingId,
            UUID offeringId,
            String offeringTitle,
            List<SessionResponse> sessions,
            String bookedAt
    ){}
}
