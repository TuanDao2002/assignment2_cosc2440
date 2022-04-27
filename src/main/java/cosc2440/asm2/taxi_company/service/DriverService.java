package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.repository.DriverRepository;
import cosc2440.asm2.taxi_company.utility.PagingUtility;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private SessionFactory sessionFactory;

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

    public ResponseEntity<List<Driver>> getDriverByEntity(String attribute, String attributeValue, int pageSize, int pageNum) {
        if (attributeValue == null || attributeValue.isEmpty()) return null;
        if (attribute == null || attribute.isEmpty()) return null;
        if (!availableAttribute.contains(attribute)) return null;

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Driver.class);
        criteria.add(Restrictions.like(attribute, attributeValue, MatchMode.ANYWHERE).ignoreCase());
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return criteria.list().isEmpty() ? null : PagingUtility.getAll((List<Driver>) criteria.list(), pageSize, pageNum);
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

        // check if driver has any car yet
        if (driverToUpdate.getCar() != null)
            return String.format("Driver with id %d already have car with VIN %d!", driverId, driverToUpdate.getCar().getVIN());

        // Assign car and driver to each other
        driverToUpdate.setCar(carToUpdate);
        carToUpdate.setDriver(driverToUpdate);

        // Update in database
        updateDriver(driverToUpdate);
        carService.updateCar(carToUpdate);

        return String.format("Assign car with VIN %d to driver with id %d!", carVIN, driverId);
    }
}
