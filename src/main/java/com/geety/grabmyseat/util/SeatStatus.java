package com.geety.grabmyseat.util;

public enum SeatStatus {
    AVAILABLE,   // Seat is free and can be booked
    HOLD,        // Temporarily blocked (payment in progress)
    BOOKED       // Final booking done
}
