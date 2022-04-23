package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Car;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.service.CarService;
import cosc2440.asm2.taxi_company.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private CarService carService;

    @RequestMapping(path = "/driver", method = RequestMethod.GET)
    public ResponseEntity<List<Driver>> getAllDrivers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "5") int size) {
        return driverService.getAllDriver(page, size);
    }

    @RequestMapping(path = "/driver", method = RequestMethod.POST)
    public String addDriver(@RequestBody Driver driver) {
        return driverService.addDriver(driver);
    }

    @RequestMapping(path = "/driver/{id}", method = RequestMethod.GET)
    public Driver getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @RequestMapping(path = "/driver/{id}", method = RequestMethod.DELETE)
    public String deleteDriverById(@PathVariable Long id) {
        return driverService.deleteDriverById(id);
    }

    @RequestMapping(path = "/driver", method = RequestMethod.PUT)
    public String updateDriver(@RequestBody Driver driver) {
        return driverService.updateDriver(driver);
    }

    @RequestMapping(path = "/driver/pick", method = RequestMethod.GET)
    public String pickCarById(@RequestParam(value = "carVIN") Long carVIN,
                              @RequestParam(value = "driverId") Long driverID) {
        Car carToUpdate = carService.getCarById(carVIN);
        Driver driverToUpdate = driverService.getDriverById(driverID);

        if (carToUpdate == null) {
            return String.format("Car with VIN %d does not exist!", carVIN);
        }

        if (driverToUpdate == null) {
            return String.format("Driver with id %d does not exist!", driverID);
        }

        // Handle more logic here (set car, set driver, set available,...)
        driverToUpdate.setCar(carToUpdate);
        carToUpdate.setDriver(driverToUpdate);
        carToUpdate.setAvailable(false);

        driverService.updateDriver(driverToUpdate);
        carService.updateCar(carToUpdate);

        return String.format("Assign car with VIN %d to driver with id %d!", carVIN, driverID);
    }
}
