package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @RequestMapping(path = "booking", method = RequestMethod.GET)
    public ResponseEntity<List<Booking>> getAllBookings(@RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        List<Booking> list = bookingService.getAll(page, size);
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(path = "/booking", method = RequestMethod.POST)
    public String addBooking(@RequestBody Booking booking) {
        return bookingService.add(booking);
    }
}
