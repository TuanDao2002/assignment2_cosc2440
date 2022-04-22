package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String invalidWarning() {
        return "Invalid datetime format.\nThe pick up date time should not be null,\nall date times have format of HH:mm:ss dd-MM-uuuu and valid datetime numbers";
    }

}
