package com.geety.grabmyseat.service;

import com.geety.grabmyseat.model.Seat;

public interface SeatBookingService {

  public Seat holdSeat(Long seatId, int holdSeconds) throws RuntimeException;
  public void confirmBooking(Long seatId) throws RuntimeException;
  public void releaseExpiredSeats();

}

