package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.repository.CarRepository;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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

    public ResponseEntity<List<Car>> getAllCar(Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<Car> pageResult = carRepository.findAll(paging);

        List<Car> list = pageResult.hasContent() ? pageResult.getContent() : new ArrayList<>();

        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    public String addCar(Car car) {
        carRepository.save(car);
        return String.format("Car with VIN %s added successfully!", car.getVIN());
    }

    public Car getCarById(Long VIN) {
        return carRepository.findById(VIN).isEmpty() ? null : carRepository.findById(VIN).get();
    }

    public String deleteCarById(Long VIN) {
        Car carToDelete = getCarById(VIN);

        if (carToDelete == null) {
            return String.format("Car with VIN %s does not exist!", VIN);
        }

        // set the car of the driver to be null
        carToDelete.getDriver().setCar(null);

        // delete car from database
        carRepository.delete(carToDelete);
        return String.format("Car with VIN %s deleted!", VIN);
    }

    public String updateCar(Car car) {
        Car carToUpdate = getCarById(car.getVIN());
        if (carToUpdate == null) {
            return String.format("Car with VIN %s does not exist!", car.getVIN());
        }

        if (car.getMake() == null) carToUpdate.setMake(car.getMake());
        if (car.getColor() == null) carToUpdate.setColor(car.getColor());
        if (car.getLicensePlate() == null) carToUpdate.setLicensePlate(car.getLicensePlate());
        if (car.getModel() == null) carToUpdate.setModel(car.getModel());

        carToUpdate.setConvertible(car.isConvertible());
        carToUpdate.setRating(car.getRating());
        carToUpdate.setRatePerKilometer(car.getRatePerKilometer());

        carRepository.save(carToUpdate);
        return String.format("Car with VIN %s updated!", car.getVIN());
    }

    public List<Car> getAllAvailableCar() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Car.class);
        criteria.add(Restrictions.eq("isAvailable", true));
        return criteria.list();
    }

}
