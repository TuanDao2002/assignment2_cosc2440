package cosc2440.asm2.taxi_company.repository;

import cosc2440.asm2.taxi_company.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {}
