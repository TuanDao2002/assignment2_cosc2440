package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.service.BookingService;
import cosc2440.asm2.taxi_company.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private InvoiceService invoiceService;

    @RequestMapping(path = "/booking", method = RequestMethod.GET)
    public ResponseEntity<List<Booking>> getAllBookings(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "5") int size) {
        return bookingService.getAll(page, size);
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

    @RequestMapping(path = "/booking/finalize", method = RequestMethod.POST)
    public String finalizeBooking(@RequestParam("bookingID") Long bookingID,
                                  @RequestParam("dropOffDatetime") String dropOffDatetime,
                                  @RequestParam("distance") int distance) {
        return bookingService.finalizeBooking(bookingID, dropOffDatetime, distance);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String invalidWarning() {
        return "Invalid datetime format.\nThe pick up date time should not be null\nAll date times have format of HH:mm:ss dd-MM-uuuu and valid datetime numbers";
    }

    @RequestMapping(path = "/invoice", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getAllInvoices(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "5") int size) {
        return invoiceService.getAll(page, size);
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

}
