package cosc2440.asm2.taxi_company.controller;

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

    @RequestMapping(path = "/admin/driver", method = RequestMethod.GET)
    public ResponseEntity<List<Driver>> getAllDrivers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "20") int size) {
        return driverService.getAllDriver(page, size);
    }

    @RequestMapping(path = "/admin/driver", method = RequestMethod.POST)
    public String addDriver(@RequestBody Driver driver) {
        return driverService.addDriver(driver);
    }

    @RequestMapping(path = "/admin/driver/{id}", method = RequestMethod.GET)
    public Driver getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @RequestMapping(path = "/admin/driver/{id}", method = RequestMethod.DELETE)
    public String deleteDriverById(@PathVariable Long id) {
        return driverService.deleteDriverById(id);
    }

    @RequestMapping(path = "/admin/driver", method = RequestMethod.PUT)
    public String updateDriver(@RequestBody Driver driver) {
        return driverService.updateDriver(driver);
    }

    @RequestMapping(path = "/driver/pick", method = RequestMethod.GET)
    public String pickCarById(@RequestParam(value = "carVIN") Long carVIN,
                              @RequestParam(value = "driverId") Long driverId) {
        return driverService.pickCarById(carVIN, driverId);
    }

    @RequestMapping(path = "/admin/driver/attribute", method = RequestMethod.GET)
    public ResponseEntity<List<Driver>> getCarByAttribute(@RequestParam(value = "attributeName") String attributeName,
                                                       @RequestParam(value = "attributeValue") String attributeValue,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "20") int size) {

        return driverService.getDriverByAttribute(attributeName, attributeValue, size, page);
    }
}
