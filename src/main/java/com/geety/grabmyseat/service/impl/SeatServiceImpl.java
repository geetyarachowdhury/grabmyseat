package com.geety.grabmyseat.service.impl;

import com.geety.grabmyseat.model.Seat;
import com.geety.grabmyseat.repository.SeatRepository;
import com.geety.grabmyseat.service.SeatService;
import com.geety.grabmyseat.util.SeatStatus;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public Seat addSeat(Seat seat) {
        // When adding a new seat, it should not be booked by default
        seat.setStatus(SeatStatus.AVAILABLE);
        return seatRepository.save(seat);
    }

    @Override
    public Seat getSeatById(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new RuntimeException("Seat not found with id: " + seatId)
                );
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @Override
    public void deleteSeat(Long seatId) {
        if (!seatRepository.existsById(seatId)) {
            throw new RuntimeException("Seat not found with id: " + seatId);
        }
        seatRepository.deleteById(seatId);
    }
}
