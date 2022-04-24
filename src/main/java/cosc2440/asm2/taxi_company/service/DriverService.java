package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarService carService;

    public void setDriverRepository(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public ResponseEntity<List<Driver>> getAllDriver(Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<Driver> pageResult = driverRepository.findAll(paging);

        List<Driver> list = pageResult.hasContent() ? pageResult.getContent() : new ArrayList<>();

        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    public String addDriver(Driver driver) {
        driverRepository.save(driver);
        return String.format("Driver with id %d added successfully!", driver.getId());
    }

    public Driver getDriverById(Long id) {
        if (id == null) return null;
        return driverRepository.findById(id).isEmpty() ? null : driverRepository.findById(id).get();
    }

    public String deleteDriverById(Long id) {
        Driver driverToDelete = getDriverById(id);

        if (driverToDelete == null) {
            return String.format("Driver with id %d does not exist!", id);
        }

        driverRepository.delete(driverToDelete);
        return String.format("Driver with id %d deleted!", id);
    }

    public String updateDriver(Driver driver) {
        Driver driverToUpdate = getDriverById(driver.getId());
        if (driverToUpdate == null) {
            return String.format("Driver with id %d does not exist!", driver.getId());
        }

        if (!driver.getLicenseNumber().isEmpty()) driverToUpdate.setLicenseNumber(driver.getLicenseNumber());
        if (!driver.getPhoneNumber().isEmpty()) driverToUpdate.setPhoneNumber(driver.getPhoneNumber());
        driverToUpdate.setRating(driver.getRating());

        driverRepository.save(driverToUpdate);
        return String.format("Driver with id %d updated!", driver.getId());
    }

    public String pickCarById(Long carVIN, Long driverId) {
        Car carToUpdate = carService.getCarById(carVIN);
        Driver driverToUpdate = getDriverById(driverId);

        if (carToUpdate == null) {
            return String.format("Car with VIN %d does not exist!", carVIN);
        }

        if (driverToUpdate == null) {
            return String.format("Driver with id %d does not exist!", driverId);
        }

        if (carToUpdate.getDriver() != null) {
            return String.format("Car with VIN %d is not available!", carVIN);
        }

        if (driverToUpdate.getCar() != null) {
            return String.format("Driver with id %d already have car with VIN %d!", driverId, driverToUpdate.getCar().getVIN());
        }

        // Assign car and driver to each other
        driverToUpdate.setCar(carToUpdate);
        carToUpdate.setDriver(driverToUpdate);

        // Update in database
        updateDriver(driverToUpdate);
        carService.updateCar(carToUpdate);

        return String.format("Assign car with VIN %d to driver with id %d!", carVIN, driverId);
    }
}
