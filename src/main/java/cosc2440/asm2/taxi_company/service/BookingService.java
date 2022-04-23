package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import cosc2440.asm2.taxi_company.repository.InvoiceRepository;
import cosc2440.asm2.taxi_company.utility.DateComparator;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private SessionFactory sessionFactory;

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void setInvoiceRepository(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Booking> searchBookingByDate(String matchPickUpDate, String startDate, String endDate) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Booking.class);

        // format of the input date from the request
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-uuuu");

        // if the Booking's pickUpDatetime is matched with matchPickUpDate, return it to client
        if (matchPickUpDate != null) {
            // convert string to LocalDate
            LocalDate match = LocalDate.parse(matchPickUpDate, dtf);

            // find all bookings in the database
            List<Booking> allBookings = (List<Booking>) bookingRepository.findAll();

            // find all bookings that match with matchPickUpDate
            List<Booking> matchBookings = new ArrayList<>();
            for (Booking booking : allBookings) {
                if (booking.getPickUpDatetimeObj().toLocalDate().equals(match)) {
                    matchBookings.add(booking);
                }
            }

            return matchBookings;
        }

        // if the Booking's pickUpDatetime is greater than or equal to startDate, return it to client
        if (startDate != null) {
            // convert string to LocalDate
            LocalDate start = LocalDate.parse(startDate, dtf);

            // find all bookings that have pickUpDatetime greater than or equal to startDate
            criteria.add(Restrictions.ge("pickUpDatetime", start.atStartOfDay()));
        }

        // if the Booking's pickUpDatetime is less than or equal to endDate, return it to client
        if (endDate != null) {
            // convert string to LocalDate
            LocalDate end = LocalDate.parse(endDate, dtf);

            // find all bookings that have pickUpDatetime less than or equal to endDate
            criteria.add(Restrictions.le("pickUpDatetime", end.atStartOfDay()));
        }

        return criteria.list();
    }

    public ResponseEntity<List<Booking>> getAll(Integer pageNumber, Integer pageSize,
                                                String matchPickUpDate, String startDate, String endDate) {

        // return empty if the retrieve Bookings are not found or the page size is 0
        List<Booking> retrievedBookingList = searchBookingByDate(matchPickUpDate, startDate, endDate);
        if (retrievedBookingList.isEmpty() || pageSize == 0) return new ResponseEntity<>(new ArrayList<>(), new HttpHeaders(), HttpStatus.OK);

        Pageable paging = PageRequest.of(pageNumber, pageSize);
        int start = (int)paging.getOffset();
        int end = Math.min((start + paging.getPageSize()), retrievedBookingList.size());
        Page<Booking> pagedResult = new PageImpl<>(retrievedBookingList.subList(start, end), paging, retrievedBookingList.size());

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

            if (!DateComparator.validateDatetimeOf(findBooking)) {
                return "The drop-off date time must be after the pick-up date time";
            }

            // if the Invoice exists in Booking, delete it from database
            if (invoiceRepository.findById(findBooking.getInvoice().getInvoiceID()).isPresent())
                invoiceRepository.delete(findBooking.getInvoice());

            // set the new Invoice for Booking and update Booking
            findBooking.setInvoice(booking.getInvoice());
            bookingRepository.save(findBooking);
            return "Booking with ID: " + booking.getBookingID() + " is updated!!!";
        }
    }

    public String createBooking(String startLocation, String endLocation, String pickUpDatetime) {
        Booking booking = new Booking(startLocation, endLocation, pickUpDatetime);
        return add(booking);
    }

    public String finalizeBooking(Long bookingID, String dropOffDatetime, int distance) {
        Booking findBooking = getOne(bookingID);

        if (findBooking == null) {
            return "Booking with ID: " + bookingID + " does not exist!!!";
        } else {
            // set the new drop off date time and distance to finalize booking
            findBooking.setDropOffDateTime(dropOffDatetime);

            if (!DateComparator.validateDatetimeOf(findBooking)) {
                return "The drop-off date time must be after the pick-up date time";
            }

            // check if the distance is greater than 0
            if (distance <= 0) {
                return "The distance must be greater than 0";
            }
            findBooking.setDistance(distance);

            // update the new modification to booking
            bookingRepository.save(findBooking);
            return "Booking with ID: " + bookingID + " is finalized!!!";
        }
    }


}
