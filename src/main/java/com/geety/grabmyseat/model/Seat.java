package com.geety.grabmyseat.model;

import java.time.LocalDateTime;

import com.geety.grabmyseat.util.SeatStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue
    private Long seatId;
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime holdUntil;

    @Version
    private Long version;

    public Long getSeatId() {
        return seatId;
    }
    public String getSeatNumber() {
        return seatNumber;
    }
    public SeatStatus getStatus() {
        return status;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public LocalDateTime getHoldUntil() {
        return holdUntil;
    }

    public void setHoldUntil(LocalDateTime holdUntil) {
        this.holdUntil = holdUntil;
    }
}
