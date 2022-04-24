package cosc2440.asm2.taxi_company.repository;

import cosc2440.asm2.taxi_company.model.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>{
}
