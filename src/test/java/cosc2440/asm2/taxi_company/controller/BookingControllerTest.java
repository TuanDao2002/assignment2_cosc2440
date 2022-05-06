package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cosc2440.asm2.taxi_company.model.*;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import cosc2440.asm2.taxi_company.repository.CarRepository;
import cosc2440.asm2.taxi_company.repository.CustomerRepository;
import cosc2440.asm2.taxi_company.repository.DriverRepository;
import cosc2440.asm2.taxi_company.service.BookingService;
import cosc2440.asm2.taxi_company.service.CustomerService;
import cosc2440.asm2.taxi_company.service.DriverService;
import cosc2440.asm2.taxi_company.utility.DateUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private DriverRepository driverRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private CarRepository carRepository;

    @InjectMocks
    @Autowired
    private BookingService bookingService;

    @InjectMocks
    @Autowired
    private DriverService driverService;

    @InjectMocks
    @Autowired
    private CustomerService customerService;

    @InjectMocks
    @Autowired
    private BookingController bookingController;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private final List<Booking> bookingList = setUpData();

    private List<Booking> setUpData(){
        Driver driver1 = new Driver(1L, "02245462","0903123456", 2);
        Customer customer1 = new Customer(1L, "Tuan", "0908198061", "binh tan district");
        Invoice invoice1 = new Invoice(1L, 198, driver1, customer1);
        driver1.getInvoiceList().add(invoice1);
        customer1.getInvoiceList().add(invoice1);

        Driver driver2 = new Driver(2L, "09245469","0903654321", 10);
        Customer customer2 = new Customer(2L, "An", "09081987652", "7 district");
        Invoice invoice2 = new Invoice(2L, 298, driver2, customer2);
        driver2.getInvoiceList().add(invoice2);
        customer2.getInvoiceList().add(invoice2);

        Invoice invoice3 = new Invoice(3L, 200, driver2, customer1);
        driver2.getInvoiceList().add(invoice3);
        customer1.getInvoiceList().add(invoice3);

        Booking booking1 = new Booking(1L, "hcm", "hanoi", "09:09:09 09-09-2022", invoice1);
        booking1.setDropOffDateTime("09:09:10 09-09-2022");
        invoice1.setBooking(booking1);

        Booking booking2 = new Booking(2L, "hanoi", "hcm", "09:09:09 08-12-2022", invoice2);
        booking2.setDropOffDateTime("09:09:10 09-12-2022");
        invoice2.setBooking(booking2);

        Booking booking3 = new Booking(3L, "long an", "can tho", "09:09:09 12-12-2022", invoice3);
        invoice3.setBooking(booking3);

        return Arrays.asList(booking1, booking2, booking3);
    }

    @BeforeEach
    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void getAllBookings() throws Exception {
        Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

        ResponseEntity<List<Booking>> expectedResponse = new ResponseEntity<>(bookingList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Booking>> actualResponse = bookingController.getAllBookings(0, 20, null, null, null);
        assertEquals(bookingList.size(), actualResponse.getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse.getBody()).containsAll(actualResponse.getBody()));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/booking")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getBookingWithFilter() throws Exception {
        Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

        // test the function of getting a booking list in a period
        List<Booking> bookingsInPeriod = new ArrayList<>();
        bookingsInPeriod.add(bookingList.get(1));
        bookingsInPeriod.add(bookingList.get(2));

        ResponseEntity<List<Booking>> expectedResponse1 = new ResponseEntity<>(bookingsInPeriod, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Booking>> actualResponse1 = bookingController.getAllBookings(0,20, null, "08-12-2022", "13-12-2022");
        assertEquals(bookingsInPeriod.size(), actualResponse1.getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse1.getBody()).containsAll(actualResponse1.getBody()));

        // test paging
        int expectedPageSize = 1;
        ResponseEntity<List<Booking>> actualResponse2 = bookingController.getAllBookings(1, 2, null, null, null);
        assertEquals(expectedPageSize, actualResponse2.getBody().size());

        // test the function of getting a booking list matched with a date
        List<Booking> matchBookingList = new ArrayList<>();
        matchBookingList.add(bookingList.get(0));

        ResponseEntity<List<Booking>> expectedResponse3 = new ResponseEntity<>(matchBookingList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Booking>> actualResponse3 = bookingController.getAllBookings(0, 20, "09-09-2022", null, null);
        assertEquals(matchBookingList.size(), actualResponse3.getBody().size());
        assertEquals(expectedResponse3, actualResponse3);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/booking?page=1&&size=2&&startDate=08-12-2022&&endDate=13-12-2022")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getBookingById() throws Exception {
        Long bookingID = 1L;
        Mockito.when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(bookingList.get(0)));
        Booking getBooking = bookingService.getOne(bookingID);
        assertNotNull(getBooking);
        assertEquals(bookingID, getBooking.getBookingID());
        assertEquals(bookingList.get(0), getBooking);

        mockMvc.perform(MockMvcRequestBuilders.get("/booking" + "/" + getBooking.getBookingID())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void addBooking() throws Exception {
        Booking newBooking = new Booking(1L, "hcm", "long an", "09:09:09 09-09-2022", null);

        assertEquals("Invoice must not be null", bookingController.addBooking(newBooking));
        Invoice newInvoice = new Invoice();
        newBooking.setInvoice(newInvoice);

        assertEquals("Driver must not be null", bookingController.addBooking(newBooking));
        Driver driver = bookingList.get(0).getInvoice().getDriver();
        newInvoice.setDriver(driver);
        newBooking.setInvoice(newInvoice);

        assertEquals("Customer must not be null", bookingController.addBooking(newBooking));
        Customer customer = bookingList.get(0).getInvoice().getCustomer();
        newInvoice.setCustomer(customer);
        newBooking.setInvoice(newInvoice);

        assertEquals("This driver does not exist", bookingController.addBooking(newBooking));
        Mockito.when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        Driver savedDriver = driverService.getDriverById(1L);
        newInvoice.setDriver(savedDriver);
        newBooking.setInvoice(newInvoice);

        assertEquals("This driver does not have a car", bookingController.addBooking(newBooking));
        Car car = new Car();
        car.setAvailable(false);
        savedDriver.setCar(car);
        newInvoice.setDriver(savedDriver);
        newBooking.setInvoice(newInvoice);

        assertEquals("This driver has other booking", bookingController.addBooking(newBooking));
        car.setAvailable(true);
        savedDriver.setCar(car);
        newInvoice.setDriver(savedDriver);
        newBooking.setInvoice(newInvoice);

        assertEquals("This customer does not exist", bookingController.addBooking(newBooking));
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Customer savedCustomer = customerService.getCustomerById(1L);
        newInvoice.setCustomer(savedCustomer);
        newBooking.setInvoice(newInvoice);

        assertEquals("The pick-up date time must be after the drop-of date time of the latest booking"
                        + DateUtility.displayDriverLatestBookingDropOff(driver),
                        bookingController.addBooking(newBooking));
        newBooking.setPickUpDatetime("09:09:11 09-09-2022");

        Mockito.when(bookingRepository.save(newBooking)).thenReturn(newBooking);
        assertEquals("Booking with id: 1 is added!!!", bookingController.addBooking(newBooking));
        assertFalse(newBooking.getInvoice().getDriver().getCar().isAvailable());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/booking").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(newBooking)))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateBooking() throws Exception {
        Invoice invoice = new Invoice();
        Booking booking = new Booking(4L, "hcm", "la", "09:09:09 09-09-2022", invoice);

        assertEquals("Booking with ID: 4 does not exist!!!", bookingController.updateBooking(booking));
        Mockito.when(bookingRepository.findById(booking.getBookingID())).thenReturn(Optional.of(booking));

        booking.setStartLocation("ho chi minh");
        booking.setEndLocation("long an");
        booking.setPickUpDatetime("09:09:09 08-09-2022");
        booking.setDropOffDateTime("09:09:09 08-09-2022");
        bookingController.updateBooking(booking);

        assertEquals("The drop-off date time must be after the pick-up date time", bookingController.updateBooking(booking));
        booking.setDropOffDateTime("09:09:09 09-09-2022");

        assertEquals("Booking with ID: 4 is updated!!!", bookingController.updateBooking(booking));

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/booking").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(booking)))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteBooking() throws Exception {
        Long bookingIdNotExist = 4L;
        Booking bookingDoesNotExist = bookingService.getOne(bookingIdNotExist);
        assertNull(bookingDoesNotExist);
        assertEquals("Booking with ID: 4 does not exist!!!", bookingController.deleteBooking(bookingIdNotExist));

        Long bookingId = 1L;
        Booking bookingExist = bookingList.get(0);
        bookingExist.setDropOffDateTime(null);
        Car car = new Car();
        car.setAvailable(false);
        bookingExist.getInvoice().getDriver().setCar(car);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingExist));

        assertFalse(car.isAvailable());
        assertEquals("Booking with ID: 1 is deleted!!!", bookingController.deleteBooking(bookingId));
        assertTrue(car.isAvailable());

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/booking" + "/" + bookingId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void bookCar() throws Exception {
        String result = bookingController.bookCar(1L, 1L, "hcm", "la", "09:09:09 32-09-2022");

        assertEquals("The pick-up date time is invalid!!!", result);
        result = bookingController.bookCar(1L, 1L, "hcm", "la", "09:09:09 09-09-2022");

        assertEquals("Car with VIN: 1 does not exist!!!", result);
        Car car = new Car();
        car.setVIN(1L);
        car.setAvailable(false);
        Mockito.when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        result = bookingController.bookCar(1L, 1L, "hcm", "la", "09:09:09 09-09-2022");

        assertEquals("This car does not have a driver", result);
        Driver driver = new Driver();
        driver.setCar(car);
        car.setDriver(driver);
        result = bookingController.bookCar(1L, 1L, "hcm", "la", "09:09:09 09-09-2022");

        assertEquals("This car is not available", result);
        car.setAvailable(true);
        result = bookingController.bookCar(1L, 1L, "hcm", "la", "09:09:09 09-09-2022");

        assertEquals("Customer with ID: 1 does not exist!!!", result);
        Customer customer = bookingList.get(1).getInvoice().getCustomer();
        Mockito.when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        result = bookingController.bookCar(1L, 2L, "hcm", "la", "09:09:09 09-09-2022");

        assertEquals("The pick-up date time must be after the drop-of date time of the latest booking"
                    + DateUtility.displayCustomerLatestBookingDropOff(customer),
                    result);

        bookingController.bookCar(1L, 2L, "hcm", "la", "09:09:11 09-12-2022");
        assertFalse(car.isAvailable()); // check whether the car is not available after booking is created

        // use Mockito.spy to create a clone copy of bookingService (due to new Booking POJO object created in the service)
        BookingService clonedBookingService = Mockito.spy(new BookingService());
        Mockito.doReturn("Booking with ID: 1 is created!!!").when(clonedBookingService).bookCar(1L, 2L, "hcm", "la", "09:09:11 09-12-2022");
        bookingController = new BookingController(clonedBookingService);
        result = bookingController.bookCar(1L, 2L, "hcm", "la", "09:09:11 09-12-2022");

        assertEquals("Booking with ID: 1 is created!!!", result);

        mockMvc.perform(MockMvcRequestBuilders.post("/booking/bookCar?carVIN=1&&customerID=2&&startLocation=hcm&&endLocation=la&&pickUpDatetime=09:09:11 09-12-2022")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void finalizeBooking() throws Exception {
        String result = bookingController.finalizeBooking(1L, "09:09:09 39-09-2022", -2);

        assertEquals("Booking with ID: 1 does not exist!!!", result);
        Booking findBooking = bookingList.get(0);
        Car car = new Car();
        car.setAvailable(false);
        findBooking.getInvoice().getDriver().setCar(car);
        car.setDriver(findBooking.getInvoice().getDriver());
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(findBooking));
        result = bookingController.finalizeBooking(1L, "39:09:09 09-09-2022", -2);

        assertEquals("Booking with ID: 1 is already finalized!!!", result);
        findBooking.setDropOffDateTime(null);
        result = bookingController.finalizeBooking(1L, "39:09:09 09-09-2022", -2);

        assertEquals("The drop off date time is invalid!!!", result);
        result = bookingController.finalizeBooking(1L, "09:09:09 09-09-2022", -2);

        assertEquals("The drop-off date time must be after the pick-up date time", result);
        result = bookingController.finalizeBooking(1L, "09:09:10 09-09-2022", -2);

        assertEquals("The distance must be greater than 0", result);
        result = bookingController.finalizeBooking(1L, "09:09:10 09-09-2022", 2);

        assertTrue(findBooking.getInvoice().getDriver().getCar().isAvailable());
        double ratePerKm = findBooking.getInvoice().getDriver().getCar().getRatePerKilometer();
        assertEquals(ratePerKm * 2, findBooking.getInvoice().getTotalCharge());

        Mockito.when(bookingRepository.save(findBooking)).thenReturn(findBooking);
        assertEquals("Booking with ID: 1 is finalized!!!", result);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/booking/finalize?bookingID=1&&dropOffDatetime=09:09:11 09-12-2022&&distance=100")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}