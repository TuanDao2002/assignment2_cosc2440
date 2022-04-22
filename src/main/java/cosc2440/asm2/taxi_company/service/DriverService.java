package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.repository.DriverRepository;
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

@Service
@Transactional
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    public void setDriverRepository(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public ResponseEntity<List<Driver>> getAllDriver(Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<Driver> pageResult = driverRepository.findAll(paging);

        List<Driver> list = pageResult.hasContent() ? pageResult.getContent() : new ArrayList<>();

        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    public String addDriver(Driver driver) {
        driverRepository.save(driver);
        return String.format("Driver with id %d added successfully!", driver.getId());
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id).isEmpty() ? null : driverRepository.findById(id).get();
    }

    public String deleteDriverById(Long id) {
        Driver driverToDelete = getDriverById(id);

        if (driverToDelete == null) {
            return String.format("Driver with id %d does not exist!", id);
        }

        driverRepository.delete(driverToDelete);
        return String.format("Driver with id %d deleted!", id);
    }

    public String updateDriver(Driver driver) {
        if (getDriverById(driver.getId()) == null) {
            return String.format("Driver with id %d does not exist!", driver.getId());
        }

        driverRepository.save(driver);
        return String.format("Driver with id %d updated!", driver.getId());
    }
}
