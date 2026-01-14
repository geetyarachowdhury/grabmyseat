package com.geety.grabmyseat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.geety.grabmyseat.model.Seat;
import com.geety.grabmyseat.service.SeatBookingService;
import com.geety.grabmyseat.util.SeatStatus;

@SpringBootTest
class GrabmyseatApplicationTests {

	@Autowired
	private SeatBookingService seatBookingService;

	// @Test
    // void multipleUsersBookingSameSeat() throws InterruptedException {

    //     int numberOfUsers = 500;

    //     ExecutorService executorService = Executors.newFixedThreadPool(50);
    //     CountDownLatch latch = new CountDownLatch(numberOfUsers);

    //     AtomicInteger successCount = new AtomicInteger(0);
    //     AtomicInteger failureCount = new AtomicInteger(0);

    //     for (int i = 0; i < numberOfUsers; i++) {
    //         executorService.submit(() -> {
    //             try {
    //                 seatBookingService.bookSeat(3L);
    //                 successCount.incrementAndGet();
    //             } catch (OptimisticLockException e) {
    //                 System.out.println("Optimistic lock failed");
    //                 failureCount.incrementAndGet();
    //             } catch (Exception e) {
    //                 System.out.println("Booking failed: " + e.getMessage());
    //                 failureCount.incrementAndGet();
    //             } finally {
    //                 latch.countDown();
    //             }
    //         });
    //     }

    //     latch.await();
    //     executorService.shutdown();

    //     System.out.println("SUCCESSFUL BOOKINGS = " + successCount.get());
    //     System.out.println("FAILED BOOKINGS = " + failureCount.get());

    
    //     assertEquals(1, successCount.get(), "Only one booking should succeed");
	// 	assertEquals(numberOfUsers - 1, failureCount.get());
    // }

    @Test
    void holdExpiresAndNextUserCanHold() throws Exception {

        // GIVEN
        Long seatId = 3L;

        int users = 2;
        ExecutorService executor = Executors.newFixedThreadPool(users);
        CountDownLatch latch = new CountDownLatch(users);
        AtomicInteger successCount = new AtomicInteger();

        // WHEN – User 1 holds the seat
        executor.submit(() -> {
            try {
                seatBookingService.holdSeat(seatId, 2); // hold for 2 sec
                successCount.incrementAndGet();
            } catch (Exception ignored) {
            } finally {
                latch.countDown();
            }
        });

        // Slight delay to ensure ordering
        Thread.sleep(500);

        // User 2 tries to hold immediately (should fail)
        executor.submit(() -> {
            try {
                seatBookingService.holdSeat(seatId, 2);
                successCount.incrementAndGet();
            } catch (Exception ignored) {
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();

        // THEN – only one should succeed initially
        assertEquals(1, successCount.get());

        // WAIT FOR EXPIRY
        Thread.sleep(2500);

        // WHEN – After expiry, another user tries
        Seat seat = seatBookingService.holdSeat(seatId, 2);

        // THEN
        assertEquals(SeatStatus.HOLD, seat.getStatus());
    }

}
