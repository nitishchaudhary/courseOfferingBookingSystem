package com.project.classOfferingBookingSystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.classOfferingBookingSystem.dto.ResponseDtos.*;
import com.project.classOfferingBookingSystem.entity.Booking;
import com.project.classOfferingBookingSystem.entity.Offering;
import com.project.classOfferingBookingSystem.entity.Session;
import com.project.classOfferingBookingSystem.entity.User;
import com.project.classOfferingBookingSystem.exception.ApplicationException;
import com.project.classOfferingBookingSystem.repository.BookingRepository;
import com.project.classOfferingBookingSystem.repository.OfferingRepository;
import com.project.classOfferingBookingSystem.repository.SessionRepository;
import com.project.classOfferingBookingSystem.repository.UserRepository;
import com.project.classOfferingBookingSystem.utils.DtoConversionUtility;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final OfferingRepository offeringRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public BookingResponse bookOffering(UUID userId, UUID offeringId) {
        User parent = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with Id " + userId));

        Offering offering = offeringRepository.findByIdWithLock(offeringId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Offering not found with Id " + offeringId));

        if (bookingRepository.existsByParentIdAndOfferingId(userId, offeringId)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "This offering has already been booked by user Id " + userId);
        }

        List<Session> sessions = offering.getSessions();
        if (sessions.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Offering with Id " + offeringId + " has no sessions");
        }

        for (Session session: sessions) {
            if (sessionRepository.checkForConflict(userId, session.getStartTime(), session.getEndTime())) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sessions are overlapping cannot process the request");
            }
        }

        Booking booking = Booking.builder()
                .parent(parent)
                .offering(offering)
                .sessions(new ArrayList<>(sessions))
                .build();
        Booking savedBooking = bookingRepository.save(booking);
        return DtoConversionUtility.bookingToBookingResponse(savedBooking, parent.getTimeZone());
    }

    @Transactional
    public List<BookingResponse> getBookings(UUID userId) {
        User parent = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with Id " + userId));

        List<Booking> bookings = bookingRepository.getBookingsByParentId(userId);
        return bookings.stream()
                .map(s -> DtoConversionUtility.bookingToBookingResponse(s, parent.getTimeZone()))
                .toList();
    }
}