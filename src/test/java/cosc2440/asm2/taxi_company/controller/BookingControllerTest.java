package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cosc2440.asm2.taxi_company.model.*;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import cosc2440.asm2.taxi_company.repository.CustomerRepository;
import cosc2440.asm2.taxi_company.repository.DriverRepository;
import cosc2440.asm2.taxi_company.service.BookingService;
import cosc2440.asm2.taxi_company.service.CustomerService;
import cosc2440.asm2.taxi_company.service.DriverService;
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
import org.springframework.test.web.servlet.MvcResult;
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

    private List<Booking> setUpData(){
        Driver driver1 = new Driver(1L, "02245462","0903123456", 2);
        Customer customer1 = new Customer(1L, "Tuan", "0908198061", "binh tan district");
        Invoice invoice1 = new Invoice(1L, 198, driver1, customer1);

        Driver driver2 = new Driver(2L, "09245469","0903654321", 10);
        Customer customer2 = new Customer(2L, "An", "09081987652", "7 district");
        Invoice invoice2 = new Invoice(2L, 298, driver2, customer2);

        Invoice invoice3 = new Invoice(3L, 200, driver2, customer1);

        return Arrays.asList(new Booking(1L, "hcm", "hanoi", "09:09:09 09-09-2022", invoice1),
                new Booking(2L, "hanoi", "hcm", "09:09:09 08-12-2022", invoice2),
                new Booking(3L, "long an", "can tho", "09:09:09 12-12-2022", invoice3));
    }

    @BeforeEach
    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void getAllBookings() throws Exception {
        List<Booking> bookingList = setUpData();
        Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

        ResponseEntity<List<Booking>> expectedResponse = new ResponseEntity<>(bookingList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Booking>> actualResponse = bookingController.getAllBookings(0, 20, null, null, null);
        assertEquals(bookingList.size(), actualResponse.getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse.getBody()).containsAll(actualResponse.getBody()));

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/booking").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getBookingWithFilter() throws Exception {
        List<Booking> bookingList = setUpData();
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

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/booking?page=1&&size=2&&startDate=08-12-2022&&endDate=13-12-2022").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getBookingById() throws Exception {
        List<Booking> bookingList = setUpData();
        Long bookingID = 1L;
        Mockito.when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(bookingList.get(0)));
        Booking getBooking = bookingService.getOne(bookingID);
        assertNotNull(getBooking);
        assertEquals(bookingID, getBooking.getBookingID());
        assertEquals(bookingList.get(0), getBooking);

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/booking" + "/" + getBooking.getBookingID()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void addBooking() throws Exception {
        Booking newBooking = new Booking( "hcm", "long an", "09:09:09 09-09-2022", null);
        Mockito.when(bookingRepository.save(newBooking)).thenReturn(newBooking);
        System.out.println(bookingRepository.save(newBooking));
        assertEquals("Invoice must not be null", bookingController.addBooking(newBooking));

        Invoice newInvoice = new Invoice();
        newBooking.setInvoice(newInvoice);
        assertEquals("Driver must not be null", bookingController.addBooking(newBooking));

        Driver driver = new Driver(1L, "12345","0908734234", 12);
        newInvoice.setDriver(driver);
        newBooking.setInvoice(newInvoice);
        assertEquals("Customer must not be null", bookingController.addBooking(newBooking));

        Customer customer = new Customer(1L, "dao kha tuan", "09082321", "hcm");
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

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/booking").contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateBooking() {
    }

    @Test
    void deleteBooking() {
    }

    @Test
    void bookCar() {
    }

    @Test
    void finalizeBooking() {
    }
}