package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.repository.BookingRepository;
import cosc2440.asm2.taxi_company.service.BookingService;
import org.junit.jupiter.api.Assertions;
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

import java.util.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingRepository bookingRepository;

    @InjectMocks
    @Autowired
    private BookingService bookingService;

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

    @Test
    void getAllBookings() throws Exception {
        List<Booking> bookingList = setUpData();
        Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

        ResponseEntity<List<Booking>> expectedResponse = new ResponseEntity<>(bookingList, new HttpHeaders(), HttpStatus.OK);
        Assertions.assertEquals(bookingList.size(), bookingController.getAllBookings(0, 20, null, null, null).getBody().size());
        Assertions.assertEquals(expectedResponse, bookingController.getAllBookings(0, 20, null, null, null));

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/booking").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
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
        Assertions.assertEquals(bookingsInPeriod.size(), actualResponse1.getBody().size());
        Assertions.assertTrue(Objects.requireNonNull(expectedResponse1.getBody()).containsAll(actualResponse1.getBody()));

        // test the function of getting a booking list matched with a date
        List<Booking> matchBookingList = new ArrayList<>();
        matchBookingList.add(bookingList.get(0));

        ResponseEntity<List<Booking>> expectedResponse2 = new ResponseEntity<>(matchBookingList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Booking>> actualResponse2 = bookingController.getAllBookings(0, 20, "09-09-2022", null, null);
        Assertions.assertEquals(matchBookingList.size(), actualResponse2.getBody().size());
        Assertions.assertEquals(expectedResponse2, actualResponse2);
    }

    @Test
    void getBookingById() {
    }

    @Test
    void addBooking() {
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