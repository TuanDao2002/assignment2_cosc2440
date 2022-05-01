package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;

    public BookingController(){}

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

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
}
