package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import cosc2440.asm2.taxi_company.utility.DateUtility;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SessionFactory sessionFactory;

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Booking> searchBookingByDate(String matchPickUpDate, String startDate, String endDate) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Booking.class);

        // search date based on a specific date
        // if the Booking's pickUpDatetime is matched with matchPickUpDate, return it to client
        if (matchPickUpDate != null) {
            // if startDate or endDate is not null, the function will not work
            if (startDate != null || endDate != null) return null;

            // convert string to LocalDate
            LocalDate match = DateUtility.StringToLocalDate(matchPickUpDate);
            if (match == null) return null;

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

        // search dates in a period
        // if the Booking's pickUpDatetime is greater than or equal to startDate, return it to client
        if (startDate != null) {
            // convert string to LocalDate
            LocalDate start = DateUtility.StringToLocalDate(startDate);
            if (start == null) return null;

            // find all bookings that have pickUpDatetime greater than or equal to startDate
            criteria.add(Restrictions.ge("pickUpDatetime", start.atStartOfDay()));
        }

        // if the Booking's pickUpDatetime is less than or equal to endDate, return it to client
        if (endDate != null) {
            // convert string to LocalDate
            LocalDate end = DateUtility.StringToLocalDate(endDate);
            if (end == null) return null;

            // find all bookings that have pickUpDatetime less than or equal to endDate
            criteria.add(Restrictions.le("pickUpDatetime", end.atStartOfDay()));
        }

        return criteria.list();
    }

    public ResponseEntity<List<Booking>> getAll(Integer pageNumber, Integer pageSize,
                                                String matchPickUpDate, String startDate, String endDate) {

        List<Booking> retrievedBookingList = searchBookingByDate(matchPickUpDate, startDate, endDate);
        // return empty if the retrieve Bookings are null or not found or the page size is less than 1 or page number is negative
        if (retrievedBookingList == null || retrievedBookingList.isEmpty() || pageSize < 1 || pageNumber < 0) {
            return new ResponseEntity<>(new ArrayList<>(), new HttpHeaders(), HttpStatus.OK);
        }

        Pageable paging = PageRequest.of(pageNumber, pageSize);
        int start = (int)paging.getOffset();
        int end = Math.min((start + paging.getPageSize()), retrievedBookingList.size());

        // return empty if the page's start index is greater than page's end index
        if (start >= end) {
            return new ResponseEntity<>(new ArrayList<>(), new HttpHeaders(), HttpStatus.OK);
        }

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
        if (!DateUtility.validateDatetimeOf(booking)) {
            return "The drop-off date time must be after the pick-up date time";
        }

        bookingRepository.save(booking);
        return "Booking with id: " + booking.getBookingID() + " is added!!!";
    }

    public Booking getOne(Long bookingID) {
        if (bookingID == null) return null;
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
            if (booking.getStartLocation() != null) findBooking.setStartLocation(booking.getStartLocation());
            if (booking.getEndLocation() != null) findBooking.setEndLocation(booking.getEndLocation());

            if (booking.getPickUpDatetime() != null) findBooking.setPickUpDatetime(booking.getPickUpDatetime());
            if (booking.getDropOffDateTime() != null) findBooking.setDropOffDateTime(booking.getDropOffDateTime());

            if (!DateUtility.validateDatetimeOf(findBooking)) {
                return "The drop-off date time must be after the pick-up date time";
            }

            // if the Invoice exists in Booking, delete it from database and update new attributes for the Invoice
            if (booking.getInvoice() != null && booking.getInvoice().getInvoiceID() != null
                    && findBooking.getInvoice() != null
                    && !booking.getInvoice().getInvoiceID().equals(findBooking.getInvoice().getInvoiceID())) {
                return "Not match invoice ID";
            } else if (booking.getInvoice() != null && booking.getInvoice().getInvoiceID() != null) {
                Invoice invoice = invoiceService.getOne(booking.getInvoice().getInvoiceID());
                if (invoice == null || findBooking.getInvoice() == null) {
                    invoice = new Invoice();
                }

                if (booking.getInvoice().getTotalCharge() != 0) {
                    invoice.setTotalCharge(booking.getInvoice().getTotalCharge());
                }

                Invoice savedInvoice = invoiceService.getInvoiceRepository().save(invoice);
                findBooking.setInvoice(savedInvoice);
            } else if (booking.getInvoice() != null && booking.getInvoice().getInvoiceID() == null){
                return "No invoice ID is specified";
            }

            // set the new Invoice for Booking and update Booking
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
            if (findBooking.getDropOffDateTime() != null) return "Booking with ID: " + bookingID + " is already finalized!!!";

            // check if the drop-off datetime has valid format
            LocalDateTime verifyDateObj = DateUtility.StringToLocalDateTime(dropOffDatetime);
            if (verifyDateObj == null) return "The drop off date time is invalid!!!";

            // set the new drop off date time and distance to finalize booking
            findBooking.setDropOffDateTime(dropOffDatetime);

            if (!DateUtility.validateDatetimeOf(findBooking)) {
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
