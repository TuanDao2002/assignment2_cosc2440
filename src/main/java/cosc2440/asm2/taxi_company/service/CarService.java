package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.repository.CarRepository;
import cosc2440.asm2.taxi_company.utility.PagingUtility;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private SessionFactory sessionFactory;

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
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Car.class);

        // add constrains to the criteria
        criteria.add(Restrictions.eq("isAvailable", true));

        // select distinct to avoid duplicate
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

}
