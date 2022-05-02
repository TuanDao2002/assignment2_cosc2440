package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.repository.DriverRepository;
import cosc2440.asm2.taxi_company.service.CarService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DriverControllerTest {
    @MockBean
    private DriverRepository driverRepository;

    @InjectMocks
    @Autowired
    private CarService carService;

    @InjectMocks
    @Autowired
    private DriverController driverController;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private final List<Driver> driverList = setUpData();

    private List<Driver> setUpData() {
        Driver driver1 = new Driver(1L, "0321000x6", "0913981333", 4.9);
        Driver driver2 = new Driver(2L, "4566106e2", "0919073118", 5.0);
        Driver driver3 = new Driver(3L, "1451007x3", "0903102346", 4.2);

        return List.of(driver1, driver2, driver3);
    }

    @BeforeEach
    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(driverController).build();
    }

    @Test
    public void getAllDrivers() throws Exception {
        Mockito.when(driverRepository.findAll()).thenReturn(driverList);

        ResponseEntity<List<Driver>> expectedResponse = new ResponseEntity<>(driverList, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Driver>> actualResponse = driverController.getAllDrivers(0, 20);
        assertEquals(driverList.size(), actualResponse.getBody().size());
        assertTrue(Objects.requireNonNull(expectedResponse.getBody()).containsAll(actualResponse.getBody()));

        mockMvc.perform(MockMvcRequestBuilders.get("/driver")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addDriver() throws Exception {
        Driver newDriver =  new Driver(4L, "557507x3", "0903102229", 3.6);
        assertEquals(String.format("Driver with id %d added successfully!", newDriver.getId()), driverController.addDriver(newDriver));
        assertEquals("Driver should not be null", driverController.addDriver(null));
    }

    @Test
    public void deleteDriver() throws Exception {
        // id not exist
        Long idNotExist = 100L;
        assertEquals(String.format("Driver with id %d does not exist!", idNotExist), driverController.deleteDriverById(idNotExist));

        // id exist
        Long idExist = 1L;
        Mockito.when(driverRepository.findById(idExist)).thenReturn(Optional.of(driverList.get(0)));

        assertEquals(String.format("Driver with id %d deleted!", idExist), driverController.deleteDriverById(idExist));

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/driver" + "/" + idExist).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getDriverById() throws Exception {

        // id not exist
        Long idNotExist = 100L;
        assertNull(driverController.getDriverById(idNotExist));

        // id exist
        Long idExist = 1L;
        Mockito.when(driverRepository.findById(idExist)).thenReturn(Optional.of(driverList.get(0)));
        Driver retrievedDriver = driverController.getDriverById(idExist);

        assertNotNull(retrievedDriver);
        assertEquals(idExist, retrievedDriver.getId());
        assertEquals(driverList.get(0), retrievedDriver);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/driver" + "/" + retrievedDriver.getId()).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());


    }


}
