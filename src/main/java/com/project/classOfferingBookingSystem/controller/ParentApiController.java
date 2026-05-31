package com.project.classOfferingBookingSystem.controller;

import com.project.classOfferingBookingSystem.dto.RequestDtos.*;
import com.project.classOfferingBookingSystem.dto.ResponseDtos.*;
import com.project.classOfferingBookingSystem.service.BookingService;
import com.project.classOfferingBookingSystem.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/parent")
public class ParentApiController {

    private final BookingService bookingService;
    private final OfferingService offeringService;

    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingResponse>> getAvailableOfferings(
            @RequestHeader("X-User-Id") UUID parentId) {
        return ResponseEntity.status(HttpStatus.OK).body(offeringService.getAvailableOfferings(parentId));
    }

    @PostMapping("/offerings/{offeringId}/book")
    public ResponseEntity<BookingResponse> bookOffering(
            @RequestHeader("X-User-Id") UUID parentId,
            @PathVariable UUID offeringId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.bookOffering(parentId, offeringId));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getBookings(
            @RequestHeader("X-User-Id") UUID parentId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookings(parentId));
    }
}