package com.project.classOfferingBookingSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.UUID;

public final class RequestDtos {
    private RequestDtos(){};

    public record CreateOfferingRequest(
            @NotBlank
            String title,
            String description
    ){}

    public record AddSessionRequest(
            @NotBlank
            String startTime,
            @NotBlank
            String endTime
    ){}
}
