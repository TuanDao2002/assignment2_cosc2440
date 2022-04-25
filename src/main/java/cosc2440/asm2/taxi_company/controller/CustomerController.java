package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Customer;


import cosc2440.asm2.taxi_company.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @RequestMapping(path = "/customer", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> getAllCustomers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "20") int size) {
        return customerService.getAllCustomers(page, size);
    }

    @RequestMapping(path = "/customer", method = RequestMethod.POST)
    public String addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    @RequestMapping(path = "/customer/{id}", method = RequestMethod.GET)
    public Customer getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @RequestMapping(path = "/customer/{id}", method = RequestMethod.DELETE)
    public String deleteCustomerById(@PathVariable Long id) {
        return customerService.deleteCustomerById(id);
    }

    @RequestMapping(path = "/customer", method = RequestMethod.PUT)
    public String updateCustomer(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }
}
