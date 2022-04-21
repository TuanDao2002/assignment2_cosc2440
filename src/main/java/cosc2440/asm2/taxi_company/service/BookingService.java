package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public ResponseEntity<List<Booking>> getAll(Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<Booking> pagedResult = bookingRepository.findAll(paging);

        List<Booking> list;

        if (pagedResult.hasContent()) {
            list = pagedResult.getContent();
        } else {
            list = new ArrayList<>();
        }

        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    public String add(Booking booking) {
        bookingRepository.save(booking);
        return "Booking with id: " + booking.getId() + " is added!!!";
    }

    public Booking getOne(Long bookingID) {
        return bookingRepository.findById(bookingID).get();
    }

    public String delete(Long bookingID) {
        Booking findBooking = getOne(bookingID);

        if (findBooking == null) {
            return "Booking with ID: " + bookingID + " does not exist!!!";
        } else {
            bookingRepository.deleteById(bookingID);
            return "Booking with ID: " + bookingID + " is deleted!!!";
        }
    }

    public String update(Booking booking) {
        if (getOne(booking.getId()) == null) {
            return "Booking with ID: " + booking.getId() + " does not exist!!!";
        } else {
            bookingRepository.save(booking);
            return "Booking with ID: " + booking.getId() + " is updated!!!";
        }
    }
}
