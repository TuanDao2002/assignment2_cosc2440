package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.repository.InvoiceRepository;
import cosc2440.asm2.taxi_company.utility.DateUtility;
import cosc2440.asm2.taxi_company.utility.PagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
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
    private BookingService bookingService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CustomerService customerService;

    public void setInvoiceRepository(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void setDriverService(DriverService driverService) {
        this.driverService = driverService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public List<Invoice> searchInvoiceByDate(String matchPickUpDate, String startDate, String endDate) {
        List<Booking> bookingListByPeriod = bookingService.searchBookingByDate(matchPickUpDate, startDate, endDate);
        List<Invoice> invoiceListByPeriod = new ArrayList<>();
        for (Booking booking : bookingListByPeriod) {
            invoiceListByPeriod.add(booking.getInvoice());
        }

        return invoiceListByPeriod;
    }

    public ResponseEntity<List<Invoice>> getAll(Integer pageNumber, Integer pageSize,
                                                String matchPickUpDate, String startDate, String endDate) {
        List<Invoice> retrieveInvoiceList = searchInvoiceByDate(matchPickUpDate, startDate, endDate);
        return PagingUtility.getAll(retrieveInvoiceList, pageSize, pageNumber);
    }

    public List<Invoice> searchInvoiceByDriverInPeriod(Long driverID, String startDate, String endDate) {
        Driver findDriver = driverService.getDriverById(driverID);
        if (findDriver == null) {
            return null;
        }

        List<Invoice> driverInvoiceList = findDriver.getInvoiceList();
        return DateUtility.invoiceListFilterByPeriod(driverInvoiceList, startDate, endDate);
    }

    public ResponseEntity<List<Invoice>> getByDriverID(Integer pageNumber, Integer pageSize,
                                                       Long driverID, String startDate, String endDate) {
        List<Invoice> retrieveInvoiceList = searchInvoiceByDriverInPeriod(driverID, startDate, endDate);
        return PagingUtility.getAll(retrieveInvoiceList, pageSize, pageNumber);
    }

    public List<Invoice> searchInvoiceByCustomerInPeriod(Long customerID, String startDate, String endDate) {
        Customer findCustomer = customerService.getCustomerById(customerID);
        if (findCustomer == null) {
            return null;
        }

        List<Invoice> customerInvoiceList = findCustomer.getInvoiceList();
        return DateUtility.invoiceListFilterByPeriod(customerInvoiceList, startDate, endDate);
    }

    public ResponseEntity<List<Invoice>> getByCustomerID(Integer pageNumber, Integer pageSize,
                                                         Long customerID, String startDate, String endDate) {
        List<Invoice> retrieveInvoiceList = searchInvoiceByCustomerInPeriod(customerID, startDate, endDate);
        return PagingUtility.getAll(retrieveInvoiceList, pageSize, pageNumber);
    }

    public String add(Invoice invoice) {
        if (invoice.getBooking() == null) return "Booking must not be null";
        if (invoice.getDriver() == null) return "Driver must not be null";
        if (invoice.getCustomer() == null) return "Customer must not be null";

        // assign driver to invoice and booking
        Driver driver = driverService.getDriverById(invoice.getDriver().getId());
        if (driver == null) {
            return "This driver does not exist";
        }

        if (driver.getCar() == null) return "This driver does not have a car";
        if (!driver.getCar().isAvailable()) return "This driver has other booking";

        Customer customer = customerService.getCustomerById(invoice.getCustomer().getId());
        if (customer == null) return "This customer does not exist";

        if (!DateUtility.checkPickUpDatetimeIsValid(customer, driver, invoice.getBooking().getPickUpDatetime())) {
            return "The pick-up date time must be after the drop-of date time of the latest booking" +
                    DateUtility.displayCustomerLatestBookingDropOff(customer) +
                    DateUtility.displayDriverLatestBookingDropOff(driver);
        }

        // set the driver's car to be not available
        driver.getCar().setAvailable(false);
        invoice.setDriver(driver);
        invoice.setCustomer(customer);

        Booking newBooking = new Booking();
        if (invoice.getBooking().getStartLocation() != null) newBooking.setStartLocation(invoice.getBooking().getStartLocation());
        if (invoice.getBooking().getEndLocation() != null) newBooking.setEndLocation(invoice.getBooking().getEndLocation());
        if (invoice.getBooking().getPickUpDatetime() != null) newBooking.setPickUpDatetime(invoice.getBooking().getPickUpDatetime());
        newBooking.setInvoice(invoice);

        // booking is the owning side => set invoice to booking first before saving invoice to database
        invoice.setBooking(newBooking);

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

            if (invoice.getTotalCharge() < 0) return "New total charge must not be less than zero";

            findInvoice.setTotalCharge(invoice.getTotalCharge());
            invoiceRepository.save(findInvoice);
            return "Invoice with ID: " + invoice.getInvoiceID() + " is updated!!!";
        }
    }

    private double getRevenueFromInvoiceList(List<Invoice> invoiceList) {
        double revenue = 0;

        for (Invoice invoice : invoiceList) {
            // if the booking is finalized
            if (invoice.getBooking().getDropOffDatetimeObj() != null)
                revenue += invoice.getTotalCharge();
        }

        return revenue;
    }

    public double getRevenue(String startDate, String endDate) {
        // get list of invoice
        List<Invoice> invoiceList = searchInvoiceByDate(null, startDate, endDate);

        // get sum of revenue
        return getRevenueFromInvoiceList(invoiceList);
    }

    public double getRevenueByDriver(String startDate, String endDate, Long driverId) {
        // get list of invoice
        List<Invoice> invoiceListByDriver = searchInvoiceByDriverInPeriod(driverId, startDate, endDate);

        if (invoiceListByDriver == null) return 0;

        // get sum of revenue
        return getRevenueFromInvoiceList(invoiceListByDriver);
    }

    public double getRevenueByCustomer(String startDate, String endDate, Long customerId) {
        // get list of invoice
        List<Invoice> invoiceListByCustomer = searchInvoiceByCustomerInPeriod(customerId, startDate, endDate);

        if (invoiceListByCustomer == null) return 0;

        // get sum of revenue
        return getRevenueFromInvoiceList(invoiceListByCustomer);
    }
}
