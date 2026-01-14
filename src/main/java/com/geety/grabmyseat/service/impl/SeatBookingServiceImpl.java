package com.geety.grabmyseat.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geety.grabmyseat.model.Seat;
import com.geety.grabmyseat.repository.SeatRepository;
import com.geety.grabmyseat.service.SeatBookingService;
import com.geety.grabmyseat.util.SeatStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class SeatBookingServiceImpl implements SeatBookingService {
    private static final Logger log =
        LoggerFactory.getLogger(SeatBookingServiceImpl.class);

    private final SeatRepository seatRepository;

    public SeatBookingServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 50)
    )
    @Transactional
    public Seat holdSeat(Long seatId, int holdSeconds) {

        log.info("Attempting to HOLD seatId={} for {} seconds", seatId, holdSeconds);

        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> {
                log.error("Seat not found for seatId={}", seatId);
                return new RuntimeException("Seat not found");
            });

        if (seat.getStatus() == SeatStatus.HOLD &&
            seat.getHoldUntil().isBefore(LocalDateTime.now())) {
            log.warn("Expired HOLD detected for seatId={}, releasing it", seatId);
            seat.setStatus(SeatStatus.AVAILABLE);
        }

        if (seat.getStatus() != SeatStatus.AVAILABLE) { 
            log.warn("SeatId={} is not available. Current status={}",
                 seatId, seat.getStatus());
            throw new RuntimeException("Seat not available");
        }

        seat.setStatus(SeatStatus.HOLD);
        seat.setHoldUntil(LocalDateTime.now().plusSeconds(holdSeconds));

        Seat savedSeat = seatRepository.save(seat);
        log.info("SeatId={} successfully HELD until {}",
             seatId, savedSeat.getHoldUntil());

        return savedSeat;
    }

    @Override
    @Transactional
    public void confirmBooking(Long seatId) {

        log.info("Attempting to CONFIRM booking for seatId={}", seatId);

        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> {
                log.error("Seat not found while confirming booking, seatId={}", seatId);
                return new RuntimeException("Seat not found");
            });

        if (seat.getStatus() != SeatStatus.HOLD ||
        seat.getHoldUntil().isBefore(LocalDateTime.now())) {

            log.warn("Booking confirmation failed for seatId={}, status={}, holdUntil={}",
                 seatId, seat.getStatus(), seat.getHoldUntil());
            throw new RuntimeException("Seat hold expired");
        }

        seat.setStatus(SeatStatus.BOOKED);
        seat.setHoldUntil(null);

        seatRepository.save(seat);
        log.info("SeatId={} successfully BOOKED", seatId);
    }

    @Override
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void releaseExpiredSeats() {

        log.info("Running scheduled job to release expired seat holds");

        List<Seat> expiredSeats =
            seatRepository.findExpiredHolds(LocalDateTime.now());

        if (expiredSeats.isEmpty()) {
            log.debug("No expired seat holds found");
            return;
        }

        for (Seat seat : expiredSeats) {
            log.warn("Releasing expired HOLD for seatId={}", seat.getSeatId());
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHoldUntil(null);
        }

        seatRepository.saveAll(expiredSeats);
        log.info("Released {} expired seat holds", expiredSeats.size());
    }
}
