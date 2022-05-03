package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.repository.CarRepository;
import cosc2440.asm2.taxi_company.utility.MonthConverter;
import cosc2440.asm2.taxi_company.utility.PagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    private static final List<String> availableAttribute = List.of("make", "model", "licensePlate");

    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public ResponseEntity<List<Car>> getAllCar(Integer pageNumber, Integer pageSize, boolean getByAvailable) {
        // get all (available) car from database
        List<Car> list = getByAvailable ? getAllAvailableCar() : (List<Car>) carRepository.findAll();

        // use paging utility class to handle paging
        return PagingUtility.getAll(list, pageSize, pageNumber);
    }

    public String addCar(Car car) {
        carRepository.save(car);
        return String.format("Car with VIN %s added successfully!", car.getVIN());
    }

    public Car getCarById(Long VIN) {
        // check if id is null
        if (VIN == null) return null;

        // return null if car does not exist, else return the car
        return carRepository.findById(VIN).isEmpty() ? null : carRepository.findById(VIN).get();
    }

    public ResponseEntity<List<Car>> getCarByAttribute(String attribute, String attributeValue, int pageSize, int pageNum) {
        if (attributeValue == null || attributeValue.isEmpty()) return null;
        if (attribute == null || attribute.isEmpty()) return null;
        if (!availableAttribute.contains(attribute)) return null;

        // Get all car from database
        Set<Car> allCars = new HashSet<>((List<Car>) carRepository.findAll());
        List<Car> carByAttribute = new ArrayList<>();

        // Check criteria and add to list
        if (attribute.equalsIgnoreCase("make")) {
            for (Car car : allCars)
                if (car.getMake().equalsIgnoreCase(attributeValue))
                    carByAttribute.add(car);
        } else if (attribute.equalsIgnoreCase("model")) {
            for (Car car : allCars)
                if (car.getModel().equalsIgnoreCase(attributeValue))
                    carByAttribute.add(car);
        } else if (attribute.equalsIgnoreCase("licensePlate")) {
            for (Car car : allCars)
                if (car.getLicensePlate().equalsIgnoreCase(attributeValue))
                    carByAttribute.add(car);
        }

        return carByAttribute.isEmpty() ? null : PagingUtility.getAll(carByAttribute, pageSize, pageNum);
    }

    public String deleteCarById(Long VIN) {
        Car carToDelete = getCarById(VIN);

        // check if car does exist
        if (carToDelete == null) return String.format("Car with VIN %s does not exist!", VIN);

        // check if car is already in a booking
        if (!carToDelete.isAvailable()) return "Cannot delete this car as it has booking";

        // set the car to null in case the driver is not null
        if (carToDelete.getDriver() != null) carToDelete.getDriver().setCar(null);

        // delete car from database
        carRepository.delete(carToDelete);
        return String.format("Car with VIN %s deleted!", VIN);
    }

    public String updateCar(Car car) {
        Car carToUpdate = getCarById(car.getVIN());

        // check if car does exist
        if (carToUpdate == null) return String.format("Car with VIN %s does not exist!", car.getVIN());

        // check if attributes is not null
        if (car.getMake() != null) carToUpdate.setMake(car.getMake());
        if (car.getColor() != null) carToUpdate.setColor(car.getColor());
        if (car.getLicensePlate() != null) carToUpdate.setLicensePlate(car.getLicensePlate());
        if (car.getModel() != null) carToUpdate.setModel(car.getModel());
        carToUpdate.setConvertible(car.isConvertible());

        // check if number attribute is not negative
        if (car.getRating() >= 0) carToUpdate.setRating(car.getRating());
        if (car.getRatePerKilometer() >= 0) carToUpdate.setRatePerKilometer(car.getRatePerKilometer());

        carRepository.save(carToUpdate);
        return String.format("Car with VIN %s updated!", car.getVIN());
    }

    public List<Car> getAllAvailableCar() {
        // get all car from database
        Set<Car> allCars = new HashSet<>((List<Car>) carRepository.findAll());

        List<Car> availableCars = new ArrayList<>();

        // add to list based on criteria
        for (Car car : allCars)
            if (car.isAvailable())
                availableCars.add(car);

        return availableCars;
    }

    public ResponseEntity<List<Map<String, Integer>>> getDayUsedOfCars(String monthString, int year, int pageSize, int pageNum) {
        // convert month in text to month in number
        int month = MonthConverter.getMonthFromString(monthString);

        // check if month and year is valid
        if (month == -1) return null;
        if (year <= 0) return null;

        // create empty list result
        List<Map<String, Integer>> result = new ArrayList<>();

        // get list all car
        List<Car> carList = (List<Car>) carRepository.findAll();
        if (carList.isEmpty()) return null;

        // loop through car list
        for (Car car : carList) {
            if (car.getDriver() == null) continue;
            // get invoice list of car
            List<Invoice> invoiceList = car.getDriver().getInvoiceList();

            // if car does not have any invoice
            if (invoiceList == null) continue;

            // create new hash map
            Map<String, Integer> map = new HashMap<>();
            // create new set
            Set<Integer> set = new HashSet<>();

            // loop though invoice list
            for (Invoice invoice : invoiceList) {
                // check if month and year match
                if (month == invoice.getBooking().getPickUpDatetimeObj().getMonth().getValue() && year == invoice.getBooking().getPickUpDatetimeObj().getYear()) {
                    // set.add(day)
                    set.add(invoice.getBooking().getPickUpDatetimeObj().getDayOfMonth());
                }
            }
            // map.put(plate, set.size)
            map.put(car.getLicensePlate(), set.size());

            // result.add(map)
            result.add(map);
        }

        // return paging(list, pageSize, pageNum)
        return PagingUtility.getAll(result, pageSize, pageNum);
    }

}
