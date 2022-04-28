package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.*;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import cosc2440.asm2.taxi_company.utility.DateUtility;
import cosc2440.asm2.taxi_company.utility.PagingUtility;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
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
    private DriverService driverService;

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SessionFactory sessionFactory;

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void setDriverService(DriverService driverService) {
        this.driverService = driverService;
    }

    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Booking> searchBookingByDate(String matchPickUpDate, String startDate, String endDate) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Booking.class);

        // search date based on a specific date
        // if the Booking's pickUpDatetime is matched with matchPickUpDate, return it to client
        if (matchPickUpDate != null) {

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

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public ResponseEntity<List<Booking>> getAll(Integer pageNumber, Integer pageSize,
                                                String matchPickUpDate, String startDate, String endDate) {

        List<Booking> retrievedBookingList = searchBookingByDate(matchPickUpDate, startDate, endDate);
        return PagingUtility.getAll(retrievedBookingList, pageSize, pageNumber);
    }

    public String add(Booking booking) {
        if (booking.getInvoice() == null) return "Invoice must not be null";
        if (booking.getInvoice().getDriver() == null) return "Driver must not be null";
        if (booking.getInvoice().getCustomer() == null) return "Customer must not be null";

        Driver driver = driverService.getDriverById(booking.getInvoice().getDriver().getId());
        if (driver == null) {
            return "This driver does not exist";
        }
        if (driver.getCar() == null) return "This driver does not have a car";
        if (!driver.getCar().isAvailable()) return "This driver has other booking";

        Customer customer = customerService.getCustomerById(booking.getInvoice().getCustomer().getId());
        if (customer == null) {
            return "This customer does not exist";
        }

        if (!DateUtility.checkPickUpDatetimeIsValid(customer, driver, booking.getPickUpDatetime())) {
            return "The pick-up date time must be after the drop-of date time of the latest booking" +
                    DateUtility.displayCustomerLatestBookingDropOff(customer) +
                    DateUtility.displayDriverLatestBookingDropOff(driver);
        }

        // booking is the owning side => no need to set invoice for booking
        // but invoice is the owning side in one-to-one relationship with driver => set driver for invoice then set the invoice to booking
        Invoice invoice = booking.getInvoice();
        driver.getCar().setAvailable(false); // set the availability of car to false
        invoice.setDriver(driver);
        invoice.setCustomer(customer);

        Booking newBooking = new Booking();
        if (booking.getStartLocation() != null) newBooking.setStartLocation(booking.getStartLocation());
        if (booking.getEndLocation() != null) newBooking.setEndLocation(booking.getEndLocation());
        if (booking.getPickUpDatetime() != null) newBooking.setPickUpDatetime(booking.getPickUpDatetime());
        newBooking.setInvoice(invoice);

        bookingRepository.save(newBooking);
        return "Booking with id: " + newBooking.getBookingID() + " is added!!!";
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
            // if the booking is not finalized, when booking is deleted, set the driver's car availability to true;
            if (findBooking.getDropOffDateTime() == null) {
                findBooking.getInvoice().getDriver().getCar().setAvailable(true);
            }
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
            // only update drop-off date time when the booking is already finalized (has drop-off date time already)
            if (booking.getDropOffDateTime() != null && findBooking.getDropOffDateTime() != null) findBooking.setDropOffDateTime(booking.getDropOffDateTime());

            if (!DateUtility.validateDatetimeOf(findBooking)) {
                return "The drop-off date time must be after the pick-up date time";
            }

            bookingRepository.save(findBooking);
            return "Booking with ID: " + booking.getBookingID() + " is updated!!!";
        }
    }

    public String bookCar(Long carVIN, Long customerID, String startLocation, String endLocation, String pickUpDatetime) {
        // check if the pick-up datetime has valid format
        LocalDateTime verifyDateObj = DateUtility.StringToLocalDateTime(pickUpDatetime);
        if (verifyDateObj == null) return "The pick-up date time is invalid!!!";

        Car findCar = carService.getCarById(carVIN);
        if (findCar == null) return "Car with VIN: " + carVIN + " does not exist!!!";
        if (findCar.getDriver() == null) return "This car does not have a driver";
        if (!findCar.isAvailable()) return "This car is not available";

        Customer findCustomer = customerService.getCustomerById(customerID);
        if (findCustomer == null) return "Customer with ID: " + carVIN + " does not exist!!!";

        if (!DateUtility.checkPickUpDatetimeIsValid(findCustomer, findCar.getDriver(), pickUpDatetime)) {
            return "The pick-up date time must be after the drop-of date time of the latest booking" +
                    DateUtility.displayCustomerLatestBookingDropOff(findCustomer) +
                    DateUtility.displayDriverLatestBookingDropOff(findCar.getDriver());
        }

        Invoice newInvoice = new Invoice(0, findCar.getDriver(), findCustomer);
        Booking newBooking = new Booking(startLocation, endLocation, pickUpDatetime, newInvoice);

        // set the driver's car to be NOT available
        findCar.setAvailable(false);

        bookingRepository.save(newBooking);
        return "Booking with ID: " + newBooking.getBookingID() + " is created!!!";
    }

    // this method is for admin only
    public String finalizeBooking(Long bookingID, String dropOffDatetime, int distance) {
        Booking findBooking = getOne(bookingID);

        if (findBooking == null) {
            return "Booking with ID: " + bookingID + " does not exist!!!";
        } else {
            if (findBooking.getDropOffDateTime() != null) return "Booking with ID: " + bookingID + " is already finalized!!!";

            // check if the drop-off datetime has valid format
            LocalDateTime verifyDateObj = DateUtility.StringToLocalDateTime(dropOffDatetime);
            if (verifyDateObj == null) return "The drop off date time is invalid!!!";

            // use a temporary Booking object to check if drop-of date time is after the pick-up date time
            Booking verifyBooking = new Booking();
            verifyBooking.setPickUpDatetime(findBooking.getPickUpDatetime());
            verifyBooking.setDropOffDateTime(dropOffDatetime);

            if (!DateUtility.validateDatetimeOf(verifyBooking)) {
                return "The drop-off date time must be after the pick-up date time";
            }

            // check if the distance is greater than 0
            if (distance <= 0) {
                return "The distance must be greater than 0";
            }

            // if all inputs are valid, assign them to the Booking
            findBooking.setDropOffDateTime(dropOffDatetime);
            findBooking.setDistance(distance);

            // set the driver's car to be available
            findBooking.getInvoice().getDriver().getCar().setAvailable(true);

            // calculate the total charge of the booking
            double ratePerKm = findBooking.getInvoice().getDriver().getCar().getRatePerKilometer();
            findBooking.getInvoice().setTotalCharge(distance * ratePerKm);

            // update the new modification to Booking
            bookingRepository.save(findBooking);
            return "Booking with ID: " + bookingID + " is finalized!!!";
        }
    }
}
