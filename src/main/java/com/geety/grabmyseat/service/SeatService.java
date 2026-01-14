package com.geety.grabmyseat.service;

import java.util.List;

import com.geety.grabmyseat.model.Seat;

public interface SeatService {
    // Add a new seat
    Seat addSeat(Seat seat);

    // Get a seat by ID
    Seat getSeatById(Long seatId);

    // Get all seats
    List<Seat> getAllSeats();

    // Delete a seat by ID
    void deleteSeat(Long seatId);
}
