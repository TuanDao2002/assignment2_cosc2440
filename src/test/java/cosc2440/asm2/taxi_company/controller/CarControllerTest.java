package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cosc2440.asm2.taxi_company.model.*;
import cosc2440.asm2.taxi_company.repository.CarRepository;
import cosc2440.asm2.taxi_company.service.BookingService;
import cosc2440.asm2.taxi_company.service.CarService;
import cosc2440.asm2.taxi_company.service.DriverService;
import org.apache.tomcat.jni.User;
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerTest {
    @MockBean
    private CarRepository carRepository;

    @InjectMocks
    @Autowired
    private CarService carService;

    @InjectMocks
    @Autowired
    private DriverService driverService;

    @InjectMocks
    @Autowired
    private BookingService bookingService;

    @InjectMocks
    @Autowired
    private CarController carController;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private final List<Car> carList = setUpData();

    @BeforeEach
    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    private List<Car> setUpData() {
        Car car1 = new Car(1L, "Mercedes", "G63", "Black", false, 5.0, "59F-23531", 100.45);
        Car car2 = new Car(2L, "Toyota", "Vios", "Red", false, 4.8, "59F-24011", 82.5);
        Car car3 = new Car(3L, "BMW", "i8", "White", true, 2.9, "59F-46154", 90.45);

        car1.setAvailable(true);
        car2.setAvailable(true);
        car3.setAvailable(false);

        Driver driver1 = new Driver(1L, "02245462","0903123456", 2);
        driver1.setCar(car1);
        car1.setDriver(driver1);

        Customer customer1 = new Customer(1L, "Tuan", "0908198061", "binh tan district");
        Invoice invoice1 = new Invoice(1L, 198, driver1, customer1);
        driver1.getInvoiceList().add(invoice1);
        customer1.getInvoiceList().add(invoice1);

        Booking booking1 = new Booking(1L, "hcm", "hanoi", "09:09:09 09-09-2022", invoice1);
        booking1.setDropOffDateTime("09:09:10 09-09-2022");
        booking1.setDistance(20);
        invoice1.setBooking(booking1);

        return List.of(car1, car2, car3);
    }

    @Test
    public void getAllCars() throws Exception {
        Mockito.when(carRepository.findAll()).thenReturn(carList);

        ResponseEntity<List<Car>> expectedResponse = new ResponseEntity<>(carList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Car>> actualResponse = carController.getAllCars(0, 20, false);
        assertEquals(carList.size(), actualResponse.getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse.getBody()).containsAll(actualResponse.getBody()));

        mockMvc.perform(MockMvcRequestBuilders.get("/car")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void addCar() throws Exception {
        Car car1 = new Car(5L, "Mercedes", "G63", "Black", false, 5.0, "59F-23531", 100.45);
        Mockito.when(carRepository.save(car1)).thenReturn(car1);
        assertEquals(String.format("Car with VIN %s added successfully!", car1.getVIN()), carController.addCar(car1));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/admin/car").contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(car1))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String stringResult = mvcResult.getResponse().getContentAsString();
        assertEquals(String.format("Car with VIN %s added successfully!", car1.getVIN()), stringResult);
    }

    @Test
    public void deleteCar() throws Exception {
        // id not exist
        Long idNotExist = 100L;
        assertEquals(String.format("Car with VIN %s does not exist!", idNotExist), carController.deleteCarById(idNotExist));

        // id exist
        Long idExist = 1L;
        Mockito.when(carRepository.findById(idExist)).thenReturn(Optional.of(carList.get(0)));

        assertEquals(String.format("Car with VIN %s deleted!", idExist), carController.deleteCarById(idExist));

        // car having booking
        Long idHavingBooking = 3L;
        Mockito.when(carRepository.findById(idHavingBooking)).thenReturn(Optional.of(carList.get(2)));

        assertEquals("Cannot delete this car as it has booking", carController.deleteCarById(idHavingBooking));


        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/car" + "/" + idExist).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCarById() throws Exception {
        // id not exist
        Long idNotExist = 100L;
        assertNull(carController.getCarById(idNotExist));

        // id exist
        Long idExist = 1L;
        Mockito.when(carRepository.findById(idExist)).thenReturn(Optional.of(carList.get(0)));
        Car retrieveCar = carController.getCarById(idExist);

        assertNotNull(retrieveCar);
        assertEquals(idExist, retrieveCar.getVIN());
        assertEquals(carList.get(0), retrieveCar);

        mockMvc.perform(MockMvcRequestBuilders.get("/car" + "/" + retrieveCar.getVIN()).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateCar() throws Exception {
        Car car = new Car(100L, "Toyota", "Vios", "Red", false, 4.8, "59F-21011", 82.5);

        assertEquals(String.format("Car with VIN %s does not exist!", car.getVIN()), carController.updateCar(car));

        Mockito.when(carRepository.findById(car.getVIN())).thenReturn(Optional.of(carList.get(0)));

        car.setRating(1.2);
        car.setColor("pink");
        car.setRatePerKilometer(200.5);

        assertEquals(String.format("Car with VIN %s updated!", car.getVIN()), carController.updateCar(car));

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/car")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(car))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCarByAttribute() throws Exception {
        Mockito.when(carRepository.findAll()).thenReturn(carList);

        ResponseEntity<List<Car>> getCar1 = carService.getCarByAttribute("licensePlate", "59F-23531", 20, 0);
        ResponseEntity<List<Car>> getCar2 = carService.getCarByAttribute("make", "Toyota", 20, 0);
        ResponseEntity<List<Car>> getCar3 = carService.getCarByAttribute("model", "i8", 20, 0);

        assertEquals(1 , getCar1.getBody().size());
        assertEquals(1 , getCar2.getBody().size());
        assertEquals(1 , getCar3.getBody().size());

        assertTrue(getCar1.getBody().contains(carList.get(0)));
        assertTrue(getCar2.getBody().contains(carList.get(1)));
        assertTrue(getCar3.getBody().contains(carList.get(2)));
        assertTrue(carList.containsAll(getCar2.getBody()));

        mockMvc.perform(MockMvcRequestBuilders.get("/car/attribute?attributeName=licensePlate&&attributeValue=0321000x6")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(getCar1)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/car/attribute?attributeName=make&attributeValue=toyota")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(getCar2)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/car/attribute?attributeName=model&&attributeValue=i8")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(getCar3)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getAllAvailableCar() throws Exception {
        Mockito.when(carRepository.findAll()).thenReturn(carList);
        ResponseEntity<List<Car>> expectedResponse = new ResponseEntity<>(carList.subList(0, 2), new HttpHeaders(), HttpStatus.OK);

        assertEquals(2, carController.getAllCars(0, 20, true).getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse.getBody()).containsAll(Objects.requireNonNull(carController.getAllCars(0, 20, true).getBody())));

        mockMvc.perform(MockMvcRequestBuilders.get("/car?getByAvailable=true")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCarByDayUsed() throws Exception {
        Mockito.when(carRepository.findAll()).thenReturn(carList);
        String month = "september";
        int year = 2022;

        // Invalid month and year
        assertNull(carController.getDayUsedOfCars(0, 20, "september", -3));
        assertNull(carController.getDayUsedOfCars(0, 20, "invalid", 2022));

        assertNotNull(carController.getDayUsedOfCars(0, 20, "september", 2022).getBody());
        assertEquals(1, carController.getDayUsedOfCars(0, 20, "september", 2022).getBody().size());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/car/day?month="+ month +"&year=" + year)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
