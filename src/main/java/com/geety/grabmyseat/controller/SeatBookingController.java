package com.geety.grabmyseat.controller;

import com.geety.grabmyseat.model.Seat;
import com.geety.grabmyseat.service.SeatBookingService;
import com.geety.grabmyseat.service.SeatService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatBookingController {

    private final SeatService seatService;
    private final SeatBookingService seatBookingService;

    public SeatBookingController(SeatService seatService, SeatBookingService seatBookingService) {
        this.seatService = seatService;
        this.seatBookingService = seatBookingService;
    }


    // Add a new seat
    @PostMapping
    public Seat addSeat(@RequestBody Seat seat) {
        return seatService.addSeat(seat);
    }

    // Get seat by ID
    @GetMapping("/{id}")
    public Seat getSeatById(@PathVariable Long id) {
        return seatService.getSeatById(id);
    }

    // Get all seats
    @GetMapping
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats();
    }

    // Delete seat by ID
    @DeleteMapping("/{id}")
    public String deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return "Seat deleted successfully";
    }

    // =========================
    // Booking APIs
    // =========================

    /**
     * 1. Hold a seat (temporary lock)
     * Example:
     * POST /api/seats/hold/3?seconds=120
     */
    @PostMapping("/hold/{seatId}")
    public ResponseEntity<Seat> holdSeat(
            @PathVariable Long seatId,
            @RequestParam(defaultValue = "120") int seconds) throws RuntimeException {

        Seat seat = seatBookingService.holdSeat(seatId, seconds);
        return ResponseEntity.ok(seat);
    }

    /**
     * 2️. Confirm booking (after payment)
     * Example:
     * POST /api/seats/confirm/3
     */
    @PostMapping("/confirm/{seatId}")
    public ResponseEntity<String> confirmBooking(@PathVariable Long seatId) throws RuntimeException {
        seatBookingService.confirmBooking(seatId);
        return ResponseEntity.ok("Seat booked successfully");
    }

    /**
     * 🔧 ADMIN ONLY
     * Manually release expired seats
     */
    @PostMapping("/release-expired")
    public ResponseEntity<String> releaseExpiredSeats() {
        seatBookingService.releaseExpiredSeats();
        return ResponseEntity.ok("Expired seats released");
    }
}
