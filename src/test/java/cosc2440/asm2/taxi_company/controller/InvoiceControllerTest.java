package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cosc2440.asm2.taxi_company.model.*;
import cosc2440.asm2.taxi_company.repository.*;
import cosc2440.asm2.taxi_company.service.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerTest {

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private InvoiceRepository invoiceRepository;

    @MockBean
    private DriverRepository driverRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @InjectMocks
    @Autowired
    private InvoiceService invoiceService;

    @InjectMocks
    @Autowired
    private InvoiceController invoiceController;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(invoiceController).build();
    }

    private final List<Invoice> invoiceList = setUpInvoiceList();
    private final List<Booking> bookingList = setUpBookingList();

    private List<Invoice> setUpInvoiceList(){
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
        booking3.setDropOffDateTime("09:09:10 12-12-2022");
        invoice3.setBooking(booking3);

        return Arrays.asList(invoice1, invoice2, invoice3);
    }

    private List<Booking> setUpBookingList() {
        List<Booking> bookingList = new ArrayList<>();
        for (Invoice invoice : invoiceList) {
            bookingList.add(invoice.getBooking());
        }

        return bookingList;
    }

    @Test
    void getAllInvoices() throws Exception {
        Mockito.when(invoiceRepository.findAll()).thenReturn(invoiceList);
        Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

        ResponseEntity<List<Invoice>> expectedResponse = new ResponseEntity<>(invoiceList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Invoice>> actualResponse = invoiceController.getAllInvoices(0, 20, null, null, null);
        assertEquals(invoiceList.size(), actualResponse.getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse.getBody()).containsAll(actualResponse.getBody()));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invoice")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getInvoiceWithFilter() throws Exception {
        Mockito.when(invoiceRepository.findAll()).thenReturn(invoiceList);
        Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

        List<Invoice> invoicesInPeriod = new ArrayList<>();
        invoicesInPeriod.add(invoiceList.get(1));
        invoicesInPeriod.add(invoiceList.get(2));

        ResponseEntity<List<Invoice>> expectedResponse1 = new ResponseEntity<>(invoicesInPeriod, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Invoice>> actualResponse1 = invoiceController.getAllInvoices(0,20, null, "08-12-2022", "13-12-2022");
        assertEquals(invoicesInPeriod.size(), actualResponse1.getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse1.getBody()).containsAll(actualResponse1.getBody()));

        int expectedPageSize = 1;
        ResponseEntity<List<Invoice>> actualResponse2 = invoiceController.getAllInvoices(1, 2, null, null, null);
        assertEquals(expectedPageSize, actualResponse2.getBody().size());

        List<Invoice> matchInvoiceList = new ArrayList<>();
        matchInvoiceList.add(invoiceList.get(0));

        ResponseEntity<List<Invoice>> expectedResponse3 = new ResponseEntity<>(matchInvoiceList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Invoice>> actualResponse3 = invoiceController.getAllInvoices(0, 20, "09-09-2022", null, null);
        assertEquals(matchInvoiceList.size(), actualResponse3.getBody().size());
        assertEquals(expectedResponse3, actualResponse3);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invoice?page=1&&size=2&&startDate=08-12-2022&&endDate=13-12-2022")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getInvoiceById() throws Exception {
        Long invoiceID = 1L;
        Mockito.when(invoiceRepository.findById(invoiceID)).thenReturn(Optional.of(invoiceList.get(0)));
        Invoice getInvoice = invoiceService.getOne(invoiceID);
        assertNotNull(getInvoice);
        assertEquals(invoiceID, getInvoice.getInvoiceID());
        assertEquals(invoiceList.get(0), getInvoice);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invoice" + "/" + getInvoice.getInvoiceID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getInvoiceByDriverID() throws Exception {
        ResponseEntity<List<Invoice>> expectedResponse = new ResponseEntity<>(new ArrayList<>(), new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Invoice>> actualResponse = invoiceController.getByDriverID(0, 20, 2L, "08-12-2022", "13-12-2022");

        assertEquals(expectedResponse.getBody().size(), actualResponse.getBody().size());
        Driver findDriver = invoiceList.get(1).getDriver();
        Mockito.when(driverRepository.findById(2L)).thenReturn(Optional.of(findDriver));
        List<Invoice> driverInvoiceListInPeriod = new ArrayList<>();
        driverInvoiceListInPeriod.add(invoiceList.get(1));
        driverInvoiceListInPeriod.add(invoiceList.get(2));

        expectedResponse = new ResponseEntity<>(driverInvoiceListInPeriod, new HttpHeaders(), HttpStatus.OK);
        actualResponse = invoiceController.getByDriverID(0, 20, 2L, "08-12-2022", "13-12-2022");
        assertEquals(expectedResponse.getBody().size(), actualResponse.getBody().size());
        assertTrue(expectedResponse.getBody().containsAll(actualResponse.getBody()));

        int expectedPageSize = 1;
        actualResponse = invoiceController.getByDriverID(1, 1, 2L, "08-12-2022", "13-12-2022");
        assertEquals(expectedPageSize, actualResponse.getBody().size());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invoice/byDriverID?driverID=2&&page=1&&size=1&&startDate=08-12-2022&&endDate=13-12-2022")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getInvoiceByCustomerID() throws Exception {
        ResponseEntity<List<Invoice>> expectedResponse = new ResponseEntity<>(new ArrayList<>(), new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Invoice>> actualResponse = invoiceController.getByCustomerID(0, 20, 2L, "08-12-2022", "13-12-2022");

        assertEquals(expectedResponse.getBody().size(), actualResponse.getBody().size());
        Customer findCustomer = invoiceList.get(1).getCustomer();
        Mockito.when(customerRepository.findById(2L)).thenReturn(Optional.of(findCustomer));
        List<Invoice> customerInvoiceListInPeriod = new ArrayList<>();
        customerInvoiceListInPeriod.add(invoiceList.get(1));

        expectedResponse = new ResponseEntity<>(customerInvoiceListInPeriod, new HttpHeaders(), HttpStatus.OK);
        actualResponse = invoiceController.getByCustomerID(0, 20, 2L, "08-12-2022", "13-12-2022");
        assertEquals(expectedResponse.getBody().size(), actualResponse.getBody().size());

        System.out.println(expectedResponse.getBody());
        System.out.println(actualResponse.getBody());
        assertTrue(expectedResponse.getBody().containsAll(actualResponse.getBody()));

        int expectedPageSize = 1;
        actualResponse = invoiceController.getByCustomerID(0, 1, 2L, "08-12-2022", "13-12-2022");
        assertEquals(expectedPageSize, actualResponse.getBody().size());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invoice/byCustomerID?customerID=2&&page=1&&size=1&&startDate=08-12-2022&&endDate=13-12-2022")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void addInvoice() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(1L);

        assertEquals("Booking must not be null", invoiceController.addInvoice(invoice));
        Booking booking = new Booking("hcm", "la", "09:09:09 09-09-2022", invoice);
        invoice.setBooking(booking);

        assertEquals("Driver must not be null", invoiceController.addInvoice(invoice));
        Driver driver = invoiceList.get(0).getDriver();
        invoice.setDriver(driver);

        assertEquals("Customer must not be null", invoiceController.addInvoice(invoice));
        Customer customer = invoiceList.get(0).getCustomer();
        invoice.setCustomer(customer);

        assertEquals("This driver does not exist", invoiceController.addInvoice(invoice));
        Mockito.when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        assertEquals("This driver does not have a car", invoiceController.addInvoice(invoice));
        Car car = new Car();
        car.setAvailable(false);
        invoice.getDriver().setCar(car);

        assertEquals("This driver has other booking", invoiceController.addInvoice(invoice));
        invoice.getDriver().getCar().setAvailable(true);

        assertEquals("This customer does not exist", invoiceController.addInvoice(invoice));
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertEquals("The pick-up date time must be after the drop-of date time of the latest booking" +
                        DateUtility.displayCustomerLatestBookingDropOff(customer) +
                        DateUtility.displayDriverLatestBookingDropOff(driver),
                invoiceController.addInvoice(invoice));
        invoice.getBooking().setPickUpDatetime("09:09:11 12-12-2022");

        Mockito.when(invoiceRepository.save(invoice)).thenReturn(invoice);
        assertEquals("Invoice with id: 1 is added!!!", invoiceController.addInvoice(invoice));
        assertFalse(invoice.getDriver().getCar().isAvailable());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/invoice").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(invoice)))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateInvoice() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(1L);
        invoice.setTotalCharge(-1);
        Booking booking = new Booking(1L, "hcm", "la", "09:09:09 12-12-2022", invoice);
        invoice.setBooking(booking);

        assertEquals("Invoice with ID: 1 does not exist!!!", invoiceController.updateInvoice(invoice));
        Mockito.when(invoiceRepository.findById(invoice.getInvoiceID())).thenReturn(Optional.of(invoice));

        assertEquals("Cannot update total charge as the booking is not finalized", invoiceController.updateInvoice(invoice));
        invoice.getBooking().setDropOffDateTime("09:09:10 13-12-2022");


        assertEquals("New total charge must not be less than zero", invoiceController.updateInvoice(invoice));
        invoice.setTotalCharge(99);

        Mockito.when(invoiceRepository.save(invoice)).thenReturn(invoice);
        assertEquals("Invoice with ID: 1 is updated!!!", invoiceController.updateInvoice(invoice));

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/invoice").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteInvoice() throws Exception {
        Long invoiceIdNotExist = 4L;
        Invoice invoiceDoesNotExist = invoiceService.getOne(invoiceIdNotExist);
        assertNull(invoiceDoesNotExist);
        assertEquals("Invoice with ID: 4 does not exist!!!", invoiceController.deleteInvoice(invoiceIdNotExist));

        Long invoiceId = 1L;
        Invoice invoiceExist = invoiceList.get(0);
        invoiceExist.getBooking().setDropOffDateTime(null);
        Car car = new Car();
        car.setAvailable(false);
        invoiceExist.getDriver().setCar(car);
        Mockito.when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoiceExist));

        assertFalse(car.isAvailable());
        assertEquals("Invoice with ID: 1 is deleted!!!", invoiceController.deleteInvoice(invoiceId));
        assertTrue(car.isAvailable());

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/invoice" + "/" + invoiceId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getRevenue() throws Exception {
        Mockito.when(invoiceRepository.findAll()).thenReturn(invoiceList);
        Mockito.when(bookingRepository.findAll()).thenReturn(bookingList);

        double revenueOfAll = 0;
        for (Invoice invoice : invoiceList) {
            revenueOfAll += invoice.getTotalCharge();
        }
        assertEquals(revenueOfAll, invoiceController.getRevenue(null, null, null, null));

        double revenueOfPeriod = 0;
        revenueOfPeriod += invoiceList.get(0).getTotalCharge();
        revenueOfPeriod += invoiceList.get(1).getTotalCharge();
        assertEquals(revenueOfPeriod, invoiceController.getRevenue("09-09-2022", "09-12-2022", null, null));

        // test get revenue by driver
        Long driverIdNotExist = 4L;
        assertEquals(0, invoiceController.getRevenue(null, null, driverIdNotExist, null));

        Long driverIdExist = 2L;
        Driver driverExist = invoiceList.get(1).getDriver();
        Mockito.when(driverRepository.findById(driverIdExist)).thenReturn(Optional.of(driverExist));

        double revenueOfDriver = 0;
        revenueOfDriver += invoiceList.get(1).getTotalCharge();
        revenueOfDriver += invoiceList.get(2).getTotalCharge();
        assertEquals(revenueOfDriver, invoiceController.getRevenue(null, null, driverIdExist, null));

        // test get revenue by customer
        Long customerIdNotExist = 10L;
        assertEquals(0, invoiceController.getRevenue(null, null, null, customerIdNotExist));

        Long customerIdExist = 1L;
        Customer customerExist = invoiceList.get(0).getCustomer();
        Mockito.when(customerRepository.findById(customerIdExist)).thenReturn(Optional.of(customerExist));

        double revenueOfCustomerInPeriod = 0;
        revenueOfCustomerInPeriod += invoiceList.get(0).getTotalCharge();
        assertEquals(revenueOfCustomerInPeriod, invoiceController.getRevenue("08-09-2022", "10-09-2022", null, customerIdExist));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invoice/revenue?startDate=08-12-2022&&endDate=13-12-2022")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}