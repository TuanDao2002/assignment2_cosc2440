package cosc2440.asm2.taxi_company.repository;

import cosc2440.asm2.taxi_company.model.Car;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface CarRepository extends PagingAndSortingRepository<Car, Long> {
}
