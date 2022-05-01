package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.repository.DriverRepository;
import cosc2440.asm2.taxi_company.utility.PagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarService carService;

    private static final List<String> availableAttribute = List.of("licenseNumber", "phoneNumber");

    public void setDriverRepository(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public ResponseEntity<List<Driver>> getAllDriver(Integer pageNumber, Integer pageSize) {
        // get all driver from database
        List<Driver> list = (List<Driver>) driverRepository.findAll();

        // use paging utility class to handle paging
        return PagingUtility.getAll(list, pageSize, pageNumber);
    }

    public ResponseEntity<List<Driver>> getDriverByAttribute(String attribute, String attributeValue, int pageSize, int pageNum) {
        if (attributeValue == null || attributeValue.isEmpty()) return null;
        if (attribute == null || attribute.isEmpty()) return null;
        if (!availableAttribute.contains(attribute)) return null;

        // Get all driver from database
        Set<Driver> allDrivers =  new HashSet<>((List<Driver>) driverRepository.findAll());
        List<Driver> driverByAttribute = new ArrayList<>();

        // Add to list based on criteria
        if (attribute.equals("licenseNumber")) {
            for (Driver driver : allDrivers)
                if (driver.getLicenseNumber().equalsIgnoreCase(attributeValue))
                    driverByAttribute.add(driver);
        } else if (attribute.equals("phoneNumber")) {
            for (Driver driver : allDrivers)
                if (driver.getPhoneNumber().equalsIgnoreCase(attributeValue))
                    driverByAttribute.add(driver);
        }

        return driverByAttribute.isEmpty() ? null : PagingUtility.getAll(driverByAttribute, pageSize, pageNum);
    }

    public String addDriver(Driver driver) {
        driverRepository.save(driver);
        return String.format("Driver with id %d added successfully!", driver.getId());
    }

    public Driver getDriverById(Long id) {
        // check if id is null
        if (id == null) return null;

        // return null if driver does not exist, else return the driver
        return driverRepository.findById(id).isEmpty() ? null : driverRepository.findById(id).get();
    }

    public String deleteDriverById(Long id) {
        Driver driverToDelete = getDriverById(id);

        // check if driver with the id does exist
        if (driverToDelete == null) return String.format("Driver with id %d does not exist!", id);

        // set the driver of the car to be null if that driver has no car
        if (driverToDelete.getCar() != null) driverToDelete.getCar().setDriver(null);

        // delete driver from database
        driverRepository.delete(driverToDelete);

        return String.format("Driver with id %d deleted!", id);
    }

    public String updateDriver(Driver driver) {
        Driver driverToUpdate = getDriverById(driver.getId());

        // check if id exist
        if (driverToUpdate == null) return String.format("Driver with id %d does not exist!", driver.getId());

        // check if attributes is null
        if (driver.getLicenseNumber() != null) driverToUpdate.setLicenseNumber(driver.getLicenseNumber());
        if (driver.getPhoneNumber() != null) driverToUpdate.setPhoneNumber(driver.getPhoneNumber());

        // check if rating is negative
        if (driver.getRating() >= 0) driverToUpdate.setRating(driver.getRating());

        driverRepository.save(driverToUpdate);

        return String.format("Driver with id %d updated!", driver.getId());
    }

    public String pickCarById(Long carVIN, Long driverId) {
        Car carToUpdate = carService.getCarById(carVIN);
        Driver driverToUpdate = getDriverById(driverId);

        // check if car vin exist
        if (carToUpdate == null) return String.format("Car with VIN %d does not exist!", carVIN);

        // check if driver id exist
        if (driverToUpdate == null) return String.format("Driver with id %d does not exist!", driverId);

        // check if car has driver
        if (carToUpdate.getDriver() != null) return String.format("Car with VIN %d is not available!", carVIN);

        // set old car driver to null
        if (driverToUpdate.getCar() != null) driverToUpdate.getCar().setDriver(null);

        // Assign car and driver to each other
        driverToUpdate.setCar(carToUpdate);
        carToUpdate.setDriver(driverToUpdate);

        // Update in database
        updateDriver(driverToUpdate);
        carService.updateCar(carToUpdate);

        return String.format("Assign car with VIN %d to driver with id %d!", carVIN, driverId);
    }
}
