package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.repository.CarRepository;
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

        carRepository.delete(carToDelete);
        return String.format("Car with VIN %s deleted!", VIN);
    }

    public String updateCar(Car car) {
        if (getCarById(car.getVIN()) == null) {
            return String.format("Car with VIN %s does not exist!", car.getVIN());
        }

        carRepository.save(car);
        return String.format("Car with VIN %s updated!", car.getVIN());
    }

}
