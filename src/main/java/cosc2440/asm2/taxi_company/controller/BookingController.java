package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.service.BookingService;
import cosc2440.asm2.taxi_company.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private InvoiceService invoiceService;

    // controller for booking
    @RequestMapping(path = "/booking", method = RequestMethod.GET)
    public ResponseEntity<List<Booking>> getAllBookings(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "20") int size,
                                                        @RequestParam(value = "matchDate", required = false) String matchDate,
                                                        @RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        return bookingService.getAll(page, size, matchDate, startDate, endDate);
    }

    @RequestMapping(path = "/booking/{bookingID}", method = RequestMethod.GET)
    public Booking getBookingById(@PathVariable Long bookingID) {
        return bookingService.getOne(bookingID);
    }

    @RequestMapping(path = "/booking", method = RequestMethod.POST)
    public String addBooking(@RequestBody Booking booking) {
        return bookingService.add(booking);
    }

    @RequestMapping(path = "/booking", method = RequestMethod.PUT)
    public String updateBooking(@RequestBody Booking booking) {
        return bookingService.update(booking);
    }

    @RequestMapping(path = "/booking/{bookingID}", method = RequestMethod.DELETE)
    public String deleteBooking(@PathVariable Long bookingID) {
        return bookingService.delete(bookingID);
    }

    @RequestMapping(path = "/booking/bookCar", method = RequestMethod.POST)
    public String bookCar(@RequestParam("carVIN") Long carVIN,
                          @RequestParam("customerID") Long customerID,
                          @RequestParam("startLocation") String startLocation,
                          @RequestParam("endLocation") String endLocation,
                          @RequestParam("pickUpDatetime") String pickUpDatetime) {
        return bookingService.bookCar(carVIN, customerID, startLocation, endLocation, pickUpDatetime);
    }

    @RequestMapping(path = "/booking/finalize", method = RequestMethod.POST)
    public String finalizeBooking(@RequestParam("bookingID") Long bookingID,
                                  @RequestParam("dropOffDatetime") String dropOffDatetime,
                                  @RequestParam("distance") int distance) {
        return bookingService.finalizeBooking(bookingID, dropOffDatetime, distance);
    }

    // controller for invoice
    @RequestMapping(path = "/invoice", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getAllInvoices(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "20") int size,
                                                        @RequestParam(value = "matchDate", required = false) String matchDate,
                                                        @RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        return invoiceService.getAll(page, size, matchDate, startDate, endDate);
    }

    @RequestMapping(path = "/invoice/{invoiceID}", method = RequestMethod.GET)
    public Invoice getInvoiceById(@PathVariable Long invoiceID) {
        return invoiceService.getOne(invoiceID);
    }

    @RequestMapping(path = "/invoice/byDriverID", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getByDriverID(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "20") int size,
                                       @RequestParam(value = "driverID") Long driverID,
                                       @RequestParam(value = "startDate", required = false) String startDate,
                                       @RequestParam(value = "endDate", required = false) String endDate) {
        return invoiceService.getByDriverID(page, size, driverID, startDate, endDate);
    }

    @RequestMapping(path = "/invoice/byCustomerID", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getByCustomerID(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "20") int size,
                                                       @RequestParam(value = "customerID") Long customerID,
                                                       @RequestParam(value = "startDate", required = false) String startDate,
                                                       @RequestParam(value = "endDate", required = false) String endDate) {
        return invoiceService.getByCustomerID(page, size, customerID, startDate, endDate);
    }

    @RequestMapping(path = "/invoice", method = RequestMethod.POST)
    public String addInvoice(@RequestBody Invoice invoice) {
        return invoiceService.add(invoice);
    }

    @RequestMapping(path = "/invoice", method = RequestMethod.PUT)
    public String updateInvoice(@RequestBody Invoice invoice) {
        return invoiceService.update(invoice);
    }

    @RequestMapping(path = "/invoice/{invoiceID}", method = RequestMethod.DELETE)
    public String deleteInvoice(@PathVariable Long invoiceID) {
        return invoiceService.delete(invoiceID);
    }

    @RequestMapping(path = "/revenue", method = RequestMethod.GET)
    public double getRevenue(@RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
                             @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate) {
        return invoiceService.getRevenue(startDate, endDate);
    }

}
