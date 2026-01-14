CREATE TABLE seats (
    seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seat_number VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    hold_until TIMESTAMP,
    version BIGINT NOT NULL
);
