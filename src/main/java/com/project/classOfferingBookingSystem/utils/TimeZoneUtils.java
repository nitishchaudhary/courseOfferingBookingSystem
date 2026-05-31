package com.project.classOfferingBookingSystem.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeZoneUtils {

    public static Instant getUTCFromLocalDateTime(String localTimeZone, String local) {
        LocalDateTime localDateTime = LocalDateTime.parse(local);
        ZoneId zone = ZoneId.of(localTimeZone);
        return localDateTime.atZone(zone).toInstant();
    }

    public static String getLocalDateTimeFromUTC(String localTimeZone, Instant utcInstant) {
        ZoneId zone = ZoneId.of(localTimeZone);
        return utcInstant.atZone(zone).toString();
    }
}
