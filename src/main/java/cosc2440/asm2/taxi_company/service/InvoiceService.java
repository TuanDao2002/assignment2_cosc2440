package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Driver;
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

    @Autowired
    private DriverService driverService;

    public void setInvoiceRepository(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public void setDriverService(DriverService driverService) {
        this.driverService = driverService;
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
        if (invoice.getBooking() == null) return "Booking must not be null";
        if (invoice.getDriver() == null) return "Driver must not be null";

        // assign driver to invoice and booking
        Driver driver = driverService.getDriverById(invoice.getDriver().getId());
        if (driver == null) {
            return "This driver does not exist";
        }

        if (driver.getCar() == null) return "This driver does not have a car";
        if (!driver.getCar().isAvailable()) return "This driver has other booking";

        // set the driver's car to be not available
        driver.getCar().setAvailable(false);
        invoice.setDriver(driver);

        // booking is the owning side => set invoice to booking first before saving invoice to database
        Booking booking = invoice.getBooking();
        booking.setInvoice(invoice);
        invoice.setBooking(booking);

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
            // if the booking of the invoice is not finalized, when invoice is deleted, set the driver's car availability to true;
            if (findInvoice.getBooking().getDropOffDateTime() == null) {
                findInvoice.getDriver().getCar().setAvailable(true);
            }

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
            if (findInvoice.getBooking().getDropOffDateTime() == null) {
                return "Cannot update total charge as the booking is not finalized";
            }

            if (invoice.getTotalCharge() > 0) findInvoice.setTotalCharge(invoice.getTotalCharge());
            invoiceRepository.save(findInvoice);
            return "Invoice with ID: " + invoice.getInvoiceID() + " is updated!!!";
        }
    }
}
