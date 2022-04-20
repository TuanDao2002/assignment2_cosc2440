package cosc2440.asm2.taxi_company.repository;

import cosc2440.asm2.taxi_company.model.Booking;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {}
