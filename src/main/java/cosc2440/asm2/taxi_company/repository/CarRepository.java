package cosc2440.asm2.taxi_company.repository;

import cosc2440.asm2.taxi_company.model.Car;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CarRepository extends PagingAndSortingRepository<Car, Long> {
}
