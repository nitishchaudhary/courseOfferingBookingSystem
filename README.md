# Class Offering Booking System

## Project Overview

This service is a small Spring Boot application for managing class offerings, sessions, and parent bookings. Teachers can create offerings and add sessions, while parents can browse available offerings and book a class offering with associated sessions.

## Tech Stack Used

- Java 21 (LTS)
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- PostgreSQL (runtime database)
- ModelMapper 3.2.6
- Lombok
- Maven wrapper (`./mvnw`)

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd classOfferingBookingSystem
   ```
2. Install or use Java 21.
3. Create a PostgreSQL database named `classDB` or change the datasource URL in `src/main/resources/application.properties`.
4. Configure environment variables for database access.
5. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Environment Variables Required

The project currently uses `src/main/resources/application.properties`, but the following environment variables are recommended for local or production override:

- `SPRING_DATASOURCE_URL` - JDBC URL for PostgreSQL (default: `jdbc:postgresql://localhost:5432/classDB`)
- `SPRING_DATASOURCE_USERNAME` - PostgreSQL username
- `SPRING_DATASOURCE_PASSWORD` - PostgreSQL password
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - e.g. `update` for development
- `SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_TIME_ZONE` - should be `UTC`

Example export:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/classDB
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

## API Documentation

### Headers

All API calls require the header:
- `X-User-Id: <UUID>`

### Teacher API

- `POST /v1/teacher/offerings`
  - Creates a new offering.
  - Request body:
    ```json
    {
      "title": "Geometry Class",
      "description": "Weekly geometry tutoring"
    }
    ```

- `POST /v1/teacher/offerings/{offeringId}/sessions`
  - Adds a session to an existing offering.
  - Request body:
    ```json
    {
      "startTime": "2026-06-01T09:00:00",
      "endTime": "2026-06-01T10:00:00"
    }
    ```
  - Times are interpreted in the teacher's configured timezone.

- `GET /v1/teacher/offerings`
  - Returns offerings created by the teacher.

### Parent API

- `GET /v1/parent/offerings`
  - Returns all available offerings with session details.

- `POST /v1/parent/offerings/{offeringId}/book`
  - Books the specified offering for the parent.
  - Parent bookings include all sessions for the offering.

- `GET /v1/parent/bookings`
  - Returns bookings for the parent.

### Response Objects

- `OfferingResponse`
  - `id`, `title`, `description`, `teacher`, `sessions`
- `SessionResponse`
  - `id`, `startTime`, `endTime`
- `BookingResponse`
  - `bookingId`, `offeringId`, `offeringTitle`, `sessions`, `bookedAt`

## Database Schema Overview

The application uses the following main tables:

- `users`
  - `id` (UUID)
  - `username`
  - `email`
  - `role` (`TEACHER` or `PARENT`)
  - `time_zone` (IANA timezone string)

- `offerings`
  - `id` (UUID)
  - `title`
  - `description`
  - `teacher_id` (foreign key to `users`)

- `sessions`
  - `id` (UUID)
  - `offering_id` (foreign key to `offerings`)
  - `start_utc` (stored as UTC instant)
  - `end_utc` (stored as UTC instant)

- `bookings`
  - `id` (UUID)
  - `offering_id` (foreign key to `offerings`)
  - `parent_id` (foreign key to `users`)
  - `booked_at` (UTC instant)
  - unique constraint on `(parent_id, offering_id)` to prevent duplicate bookings

- `booking_sessions`
  - join table linking `bookings` and `sessions`

## Assumptions Made

- `X-User-Id` header is used as the user identity for all requests.
- Teachers and parents are stored in the same `users` table.
- Teachers may only manage their own offerings.
- Parents may only see and book available offerings, and may not book the same offering twice.
- Session times are submitted in the teacher's timezone and converted to UTC for storage.
- No authentication or authorization tokens are implemented beyond role checks and header-based identity.
- Payment, cancellation, and capacity management are out of scope.

## Concurrency Handling Approach

- `OfferingRepository.findByIdWithLock` uses a `PESSIMISTIC_WRITE` lock when a parent books an offering.
- This prevents concurrent updates to the same offering during booking.
- The booking flow also verifies:
  - the parent has not already booked the same offering,
  - no session conflict exists for the parent in existing bookings.
- Session conflicts are detected with a JPA query that checks overlapping intervals on the parent’s booked sessions.

## Timezone Handling Approach

- All timestamps are stored in UTC in the database.
- `spring.jpa.properties.hibernate.jdbc.time_zone=UTC` ensures JDBC reads/writes use UTC.
- Each `User` has a `timeZone` field containing an IANA zone ID.
- Incoming session times are parsed from the teacher’s local timezone and converted to UTC via `TimeZoneUtils.getUTCFromLocalDateTime(...)`.
- Responses convert UTC instants back to the user’s local timezone with `TimeZoneUtils.getLocalDateTimeFromUTC(...)`.

## Steps to Run the Application Locally

1. Ensure Java 21 is installed.
2. Start PostgreSQL and create the database used by the app:
   ```bash
   createdb classDB
   ```
3. Configure access credentials in environment variables or in `src/main/resources/application.properties`.
4. Run the app with Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Access the API at `http://localhost:8080`.

## Useful Commands

- Run the application:
  ```bash
  ./mvnw spring-boot:run
  ```
- Build and test:
  ```bash
  ./mvnw test
  ```
- Package the JAR:
  ```bash
  ./mvnw clean package
  ```
