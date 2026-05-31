package com.project.classOfferingBookingSystem.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.classOfferingBookingSystem.dto.RequestDtos.*;
import com.project.classOfferingBookingSystem.dto.ResponseDtos.*;
import com.project.classOfferingBookingSystem.entity.Offering;
import com.project.classOfferingBookingSystem.entity.Session;
import com.project.classOfferingBookingSystem.entity.User;
import com.project.classOfferingBookingSystem.enums.UserRole;
import com.project.classOfferingBookingSystem.exception.ApplicationException;
import com.project.classOfferingBookingSystem.repository.OfferingRepository;
import com.project.classOfferingBookingSystem.repository.SessionRepository;
import com.project.classOfferingBookingSystem.repository.UserRepository;
import com.project.classOfferingBookingSystem.utils.DtoConversionUtility;
import com.project.classOfferingBookingSystem.utils.TimeZoneUtils;

@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SessionRepository sessionRepository;

    @Transactional
    public OfferingResponse createOffering(UUID userId, CreateOfferingRequest request) {
        User teacher = userRepository.findById(userId)
                        .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with Id " + userId));
        if (!teacher.getRole().equals(UserRole.TEACHER)) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Only teachers can create offerings");
        }

        Offering offering = Offering.builder()
                .title(request.title())
                .description(request.description())
                .teacher(teacher)
                .build();

        Offering savedOffering  = offeringRepository.save(offering);
        return DtoConversionUtility.offeringToOfferingResponse(savedOffering, teacher.getTimeZone());
    }

    @Transactional
    public SessionResponse addSession(UUID userId, UUID offeringId, AddSessionRequest request) {
        User teacher = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with ID " + userId));
        if (!teacher.getRole().equals(UserRole.TEACHER)) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Only teachers can create offerings");
        }

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Offering not found with ID " + offeringId));
        if (!offering.getTeacher().getId().equals(userId)) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Only teachers who own the offering allowed to add session");
        }

        Instant utcStartTime = TimeZoneUtils.getUTCFromLocalDateTime(teacher.getTimeZone(), request.startTime());
        Instant utcEndTime = TimeZoneUtils.getUTCFromLocalDateTime(teacher.getTimeZone(), request.endTime());
        if (utcStartTime.isAfter(utcEndTime)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "End time must be greater than start time");
        }

        Session session = Session.builder()
                .offering(offering)
                .startTime(utcStartTime)
                .endTime(utcEndTime)
                .build();
        Session saved = sessionRepository.save(session);
        return DtoConversionUtility.sessionToSessionResponse(saved, teacher.getTimeZone());
    }

    @Transactional()
    public List<OfferingResponse> getTeacherOfferings(UUID userId) {
        User teacher = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with ID " + userId));
        if (!teacher.getRole().equals(UserRole.TEACHER)) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Only teachers can create offerings");
        }

        List<Offering> offeringList = offeringRepository.getOfferingByTeacher(userId);
        return offeringList.stream()
                .map(s -> DtoConversionUtility.offeringToOfferingResponse(s, teacher.getTimeZone()))
                .toList();
    }

    @Transactional()
    public List<OfferingResponse> getAvailableOfferings(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with ID " + userId));
        
        List<Offering> availableOfferings = offeringRepository.findAllWithSessions();
        return availableOfferings.stream()
                .map(o -> DtoConversionUtility.offeringToOfferingResponse(o, user.getTimeZone()))
                .toList();
    }
}
