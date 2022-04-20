package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @RequestMapping(path = "/booking", method = RequestMethod.POST)
    public String addBooking(@RequestBody Booking booking) {
        return bookingService.add(booking);
    }
}
