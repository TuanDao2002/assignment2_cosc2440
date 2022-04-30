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
                                                          @RequestParam(value = "size", defaultValue = "20") int size,
                                                          @RequestParam(value = "name", required = false) String name,
                                                          @RequestParam(value = "address", required = false) String address,
                                                          @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
        return customerService.getAllCustomers(page, size, name, address, phoneNumber);
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

    @RequestMapping(path = "/customer/attribute", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> getCustomerByAttribute(@RequestParam(value = "attributeName") String attributeName,
                                                          @RequestParam(value = "attributeValue") String attributeValue,
                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "size", defaultValue = "20") int size) {

        return customerService.getCustomerByAttribute(attributeName, attributeValue, size, page);
    }
}
