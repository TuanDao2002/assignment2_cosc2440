package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public void setInvoiceRepository(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public InvoiceRepository getInvoiceRepository() {
        return invoiceRepository;
    }

    public ResponseEntity<List<Invoice>> getAll(Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<Invoice> pagedResult = invoiceRepository.findAll(paging);

        List<Invoice> list;

        if (pagedResult.hasContent()) {
            list = pagedResult.getContent();
        } else {
            list = new ArrayList<>();
        }

        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    public String add(Invoice invoice) {
        invoiceRepository.save(invoice);
        return "Invoice with id: " + invoice.getInvoiceID() + " is added!!!";
    }

    public Invoice getOne(Long invoiceID) {
        if (invoiceRepository.findById(invoiceID).isPresent()) {
            return invoiceRepository.findById(invoiceID).get();
        } else {
            return null;
        }
    }

    public String delete(Long InvoiceID) {
        Invoice findInvoice = getOne(InvoiceID);

        if (findInvoice == null) {
            return "Invoice with ID: " + InvoiceID + " does not exist!!!";
        } else {
            // set the Invoice object in Booking to be null
            findInvoice.getBooking().setInvoice(null);

            // delete the Invoice from database
            invoiceRepository.deleteById(InvoiceID);
            return "Invoice with ID: " + InvoiceID + " is deleted!!!";
        }
    }

    public String update(Invoice invoice) {
        Invoice findInvoice = getOne(invoice.getInvoiceID());

        if (findInvoice == null) {
            return "Invoice with ID: " + invoice.getInvoiceID() + " does not exist!!!";
        } else {
            if (invoice.getTotalCharge() > 0) findInvoice.setTotalCharge(invoice.getTotalCharge());
            invoiceRepository.save(findInvoice);
            return "Invoice with ID: " + invoice.getInvoiceID() + " is updated!!!";
        }
    }
}
