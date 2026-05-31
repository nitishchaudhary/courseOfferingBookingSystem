package com.project.classOfferingBookingSystem.utils;

import com.project.classOfferingBookingSystem.dto.ResponseDtos;
import com.project.classOfferingBookingSystem.entity.Booking;
import com.project.classOfferingBookingSystem.entity.Offering;
import com.project.classOfferingBookingSystem.entity.Session;

import java.util.List;

public class DtoConversionUtility{
    public static ResponseDtos.SessionResponse sessionToSessionResponse(Session session, String timeZone) {
        return new ResponseDtos.SessionResponse(
                session.getId(),
                TimeZoneUtils.getLocalDateTimeFromUTC(timeZone, session.getStartTime()),
                TimeZoneUtils.getLocalDateTimeFromUTC(timeZone, session.getEndTime())
        );
    }

    public static ResponseDtos.OfferingResponse offeringToOfferingResponse(Offering offering, String timeZone) {
        List<ResponseDtos.SessionResponse> sessionResponse = offering.getSessions().stream()
                .map(s -> sessionToSessionResponse(s, timeZone))
                .toList();
        return new ResponseDtos.OfferingResponse(
                offering.getId(),
                offering.getTitle(),
                offering.getDescription(),
                offering.getTeacher().getUsername(),
                sessionResponse
        );
    }

    public static ResponseDtos.BookingResponse bookingToBookingResponse(Booking booking, String timeZone) {
        List<ResponseDtos.SessionResponse> sessionResponse = booking.getSessions().stream()
                .map(s -> sessionToSessionResponse(s, timeZone))
                .toList();
        Offering offering = booking.getOffering();
        return new ResponseDtos.BookingResponse(
                booking.getId(),
                offering.getId(),
                offering.getTitle(),
                sessionResponse,
                TimeZoneUtils.getLocalDateTimeFromUTC(timeZone, booking.getBookedAt())
        );
    }
}
