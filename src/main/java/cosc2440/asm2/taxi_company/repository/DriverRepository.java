package cosc2440.asm2.taxi_company.repository;

import cosc2440.asm2.taxi_company.model.Driver;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DriverRepository extends PagingAndSortingRepository<Driver, Long> {
}
