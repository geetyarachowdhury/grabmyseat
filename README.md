# GrabMySeat – Concurrent Seat Booking System

 ### Author: Geety Ara Chowdhury
### Tech Stack:
* Java 21
* Spring Boot
* Spring Data JPA
* H2 (In-Memory DB for demo/testing)
* JUnit 5
* GitHub Actions (CI)

## Overview
GrabMySeat is a backend system inspired by real-world ticket booking platforms like BookMyShow.

## The project focuses on:
* High-concurrency seat booking
* Data consistency under race conditions
* Seat hold with expiry
* Optimistic locking & retry mechanisms
* Scheduled cleanup of expired holds

## Core Features
 1. Seat Hold with Expiry
    * Seats can be temporarily held for a configurable time
    * Hold automatically expires if not confirmed

 2. Confirm Booking
    * Only held seats can be confirmed
    * Prevents double booking

 3. Optimistic Locking
    * Uses @Version to detect concurrent updates
    * Prevents lost updates without blocking DB threads

 4. Retry Mechanism
    * Automatically retries on optimistic lock failures
    * Mimics real BookMyShow-style retry behavior

 5. Scheduled Cleanup
    * Background job releases expired seat holds

 6. Concurrency Testing
    * Simulates multiple users attempting to book the same seat
    * Ensures only one booking succeeds


## Seat State Lifecycle

```text
 AVAILABLE  -> HOLD  -> BOOKED
     ↑           |
     └───────────┘  (on expiry / release)
```
## Database Model
 **Seat:**
   * id (Long)
   * seatNumber (String)
   * status (AVAILABLE | HOLD | BOOKED)
   * holdUntil (LocalDateTime)
   * version (Optimistic Locking)
     
## REST APIs
```text
 Hold Seat
   POST /seats/hold/{seatId}?seconds=10

 Confirm Booking
   POST /seats/confirm/{seatId}

 Release Seat (Manual Cancel)
   POST /seats/release/{seatId}
```
## Concurrency Handling
 * Optimistic locking using @Version
 * AtomicInteger used in tests to count successful bookings
 * CountDownLatch ensures all threads start together
 * ExecutorService simulates concurrent users

## Result:
   * Multiple concurrent requests
   * Only one booking succeeds
   * Others fail safely
     
## Testing Strategy
 **Unit Tests:**
   * Service-level validation

 **Concurrency Tests:**
   * 100+ parallel threads (configurable)
   * Verifies race condition safety

 **Integration Tests:**
   * Full context loading
   * Real DB interactions

## CI/CD
 **GitHub Actions:**
   * Runs on every push & pull request
   * Uses Java 21 (Temurin)
   * Executes mvn clean verify

### Why Optimistic Locking?
* Avoids database-level blocking
* Scales better under high traffic
* Ideal for read-heavy booking systems

### Pessimistic locking was intentionally avoided to prevent:
   * Thread starvation
   * Reduced throughput
     
## How to Run
1. Clone the repository
2. Ensure Java 21 is installed
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. Access H2 Console:
```bash
       http://localhost:8080/h2-console
```
#### Future Enhancements
 * Redis-based distributed locking
 * Kafka event publishing
 * PostgreSQL integration
 * REST API rate limiting
 * Swagger/OpenAPI documentation


### Final Note

 **This project demonstrates:**
   * Real-world concurrency problems
   * Correct transactional design
   * Clean architecture
