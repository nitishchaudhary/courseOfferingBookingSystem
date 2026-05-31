package com.project.classOfferingBookingSystem.controller;

import com.project.classOfferingBookingSystem.dto.RequestDtos.*;
import com.project.classOfferingBookingSystem.dto.ResponseDtos.*;
import com.project.classOfferingBookingSystem.service.OfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/teacher")
@RequiredArgsConstructor
public class TeacherApiController {

    private final OfferingService offeringService;

    @PostMapping("/offerings")
    public ResponseEntity<OfferingResponse> createOffering(
            @RequestHeader("X-User-Id") UUID teacherId,
            @Valid @RequestBody CreateOfferingRequest offeringRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offeringService.createOffering(teacherId, offeringRequest));
    }

    @PostMapping("/offerings/{offeringId}/sessions")
    public ResponseEntity<SessionResponse> addSession(
            @RequestHeader("X-User-Id") UUID teacherId,
            @PathVariable UUID offeringId,
            @Valid @RequestBody AddSessionRequest sessionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offeringService.addSession(teacherId, offeringId, sessionRequest));
    }

    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingResponse>> getTeacherOfferings(
            @RequestHeader("X-User-Id") UUID teacherId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offeringService.getTeacherOfferings(teacherId));
    }
}