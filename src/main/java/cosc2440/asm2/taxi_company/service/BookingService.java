package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import cosc2440.asm2.taxi_company.repository.InvoiceRepository;
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

    @Autowired
    private InvoiceRepository invoiceRepository;

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void setInvoiceRepository(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
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
        return "Booking with id: " + booking.getBookingID() + " is added!!!";
    }

    public Booking getOne(Long bookingID) {
        if (bookingRepository.findById(bookingID).isPresent()) {
            return bookingRepository.findById(bookingID).get();
        } else {
            return null;
        }
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
        Booking findBooking = getOne(booking.getBookingID());
        if (findBooking == null) {
            return "Booking with ID: " + booking.getBookingID() + " does not exist!!!";
        } else {
            // set new attributes for updated Booking
            findBooking.setStartLocation(booking.getStartLocation());
            findBooking.setEndLocation(booking.getEndLocation());
            findBooking.setPickUpDatetime(booking.getPickUpDatetime());
            findBooking.setDropOffDateTime(booking.getDropOffDateTime());

            // delete the old Invoice from database
            invoiceRepository.delete(findBooking.getInvoice());

            // set the new Invoice for Booking and update Booking
            findBooking.setInvoice(booking.getInvoice());
            bookingRepository.save(findBooking);
            return "Booking with ID: " + booking.getBookingID() + " is updated!!!";
        }
    }
}
