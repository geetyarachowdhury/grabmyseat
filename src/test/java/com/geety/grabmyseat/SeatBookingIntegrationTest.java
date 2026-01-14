package com.geety.grabmyseat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.geety.grabmyseat.model.Seat;
import com.geety.grabmyseat.service.SeatBookingService;
import com.geety.grabmyseat.util.SeatStatus;

@SpringBootTest
@Transactional
class SeatBookingIntegrationTest {

    @Autowired
    private SeatBookingService seatBookingService;

    @Test
    void seatHoldConfirmFlow_shouldWorkCorrectly() {

        // 1️. Hold seat
        Seat heldSeat = seatBookingService.holdSeat(1L, 60);
        assertEquals(SeatStatus.HOLD, heldSeat.getStatus());

        // 2️. Another user tries to hold same seat → FAIL
        Exception exception = assertThrows(RuntimeException.class, () -> {
            seatBookingService.holdSeat(1L, 60);
        });
        assertTrue(exception.getMessage().contains("not available"));

        // 3️. Confirm booking
        seatBookingService.confirmBooking(1L);

        // 4️. Seat should be BOOKED
        Exception confirmAgain = assertThrows(RuntimeException.class, () -> {
            seatBookingService.holdSeat(1L, 60);
        });

        assertTrue(confirmAgain.getMessage().contains("not available"));
    }
}
