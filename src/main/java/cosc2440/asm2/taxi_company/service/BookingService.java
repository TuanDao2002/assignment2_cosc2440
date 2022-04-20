package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public String add(Booking booking) {
        bookingRepository.save(booking);
        return "Booking with id: " + booking.getId() + " is added!!!";
    }
}
