package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CarController {
    @Autowired
    private CarService carService;


    @RequestMapping(path = "/admin/car", method = RequestMethod.GET)
    public ResponseEntity<List<Car>> getAllCars(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "20") int size,
                                                @RequestParam(value = "getByAvailable", required = false, defaultValue = "false") boolean getByAvailable) {
        return carService.getAllCar(page, size, getByAvailable);
    }

    @RequestMapping(path = "/admin/car", method = RequestMethod.POST)
    public String addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }

    @RequestMapping(path = "/admin/car/{VIN}", method = RequestMethod.GET)
    public Car getCarById(@PathVariable Long VIN) {
        return carService.getCarById(VIN);
    }

    @RequestMapping(path = "/admin/car/{VIN}", method = RequestMethod.DELETE)
    public String deleteCarById(@PathVariable Long VIN) {
        return carService.deleteCarById(VIN);
    }

    @RequestMapping(path = "/admin/car", method = RequestMethod.PUT)
    public String updateCar(@RequestBody Car car) {
        return carService.updateCar(car);
    }

    @RequestMapping(path = "/admin/car/day", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Integer>>> getDayUsedOfCars(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "20") int size,
                                                       @RequestParam(value = "month") String month,
                                                       @RequestParam(value = "year") int year) {
        return carService.getDayUsedOfCars(month, year, size, page);
    }

    @RequestMapping(path = "/admin/car/attribute", method = RequestMethod.GET)
    public ResponseEntity<List<Car>> getCarByAttribute(@RequestParam(value = "attributeName") String attributeName,
                                       @RequestParam(value = "attributeValue") String attributeValue,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "20") int size) {

        return carService.getCarByAttribute(attributeName, attributeValue, size, page);
    }
}
