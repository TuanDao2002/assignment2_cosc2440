package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CarController {
    @Autowired
    private CarService carService;


    @RequestMapping(path = "/car", method = RequestMethod.GET)
    public ResponseEntity<List<Car>> getAllCars(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "5") int size) {
        return carService.getAllCar(page, size);
    }

    @RequestMapping(path = "/car", method = RequestMethod.POST)
    public String addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }

    @RequestMapping(path = "/car/{VIN}", method = RequestMethod.GET)
    public Car getCarById(@PathVariable Long VIN) {
        return carService.getCarById(VIN);
    }

    @RequestMapping(path = "/car/{VIN}", method = RequestMethod.DELETE)
    public String deleteCarById(@PathVariable Long VIN) {
        return carService.deleteCarById(VIN);
    }

    @RequestMapping(path = "/car", method = RequestMethod.PUT)
    public String updateCar(@RequestBody Car car) {
        return carService.updateCar(car);
    }

    @RequestMapping(path = "car/available", method = RequestMethod.GET)
    public List<Car> getAllAvailableCar() {
        return carService.getAllAvailableCar();
    }
}
