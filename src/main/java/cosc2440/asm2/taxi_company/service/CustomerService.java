package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.repository.CustomerRepository;
import cosc2440.asm2.taxi_company.utility.CustomerUtility;
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
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public ResponseEntity<List<Customer>> getAllCustomers(Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<Customer> pageResult = customerRepository.findAll(paging);

        List<Customer> list = pageResult.hasContent() ? pageResult.getContent() : new ArrayList<>();

        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    public String addCustomer(Customer customer) {
        customerRepository.save(customer);
        return String.format("Customer with id %d added successfully!", customer.getId());
    }

    public Customer getCustomerById(Long id) {
        if (id == null) return null;
        return customerRepository.findById(id).isEmpty() ? null : customerRepository.findById(id).get();
    }

    public String deleteCustomerById(Long id) {
        Customer customerToDelete = getCustomerById(id);

        if (customerToDelete == null) {
            return String.format("Customer with id %d does not exist!", id);
        }

        if (!CustomerUtility.checkCustomerBookingIsFinalized(customerToDelete)) {
            // if the latest booking of the customer is not finalized, delete the customer will allow the car to be available
            List<Invoice> customerInvoiceList = customerToDelete.getInvoiceList();
            Booking latestBooking = customerInvoiceList.get(customerInvoiceList.size() - 1).getBooking();
            latestBooking.getInvoice().getDriver().getCar().setAvailable(true);
        }

        customerRepository.delete(customerToDelete);
        return String.format("Customer with id %d deleted!", id);
    }

    public String updateCustomer(Customer customer) {
        Customer customerToUpdate = getCustomerById(customer.getId());
        if (customerToUpdate == null) {
            return String.format("Customer with id %d does not exist!", customer.getId());
        }

        if (customer.getName() != null) customerToUpdate.setName(customer.getName());
        if (customer.getPhoneNumber() != null) customerToUpdate.setPhoneNumber(customer.getPhoneNumber());
        if (customer.getAddress() != null) customerToUpdate.setAddress(customer.getAddress());

        customerRepository.save(customerToUpdate);
        return String.format("Customer with id %d updated!", customer.getId());
    }

}
