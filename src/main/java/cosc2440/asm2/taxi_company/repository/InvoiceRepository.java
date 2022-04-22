package cosc2440.asm2.taxi_company.repository;

import cosc2440.asm2.taxi_company.model.Invoice;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {
}
