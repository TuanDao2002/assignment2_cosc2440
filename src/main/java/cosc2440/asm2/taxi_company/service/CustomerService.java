package cosc2440.asm2.taxi_company.service;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.repository.CustomerRepository;
import cosc2440.asm2.taxi_company.utility.CustomerUtility;
import cosc2440.asm2.taxi_company.utility.PagingUtility;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private static final List<String> availableAttribute = List.of("name", "phoneNumber", "address");

    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Customer> searchCustomerBy(String name, String address, String phoneNumber) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Customer.class);

        // find match string with case in-sensitive
        if (name != null) {
            criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
        }

        if (address != null) {
            criteria.add(Restrictions.ilike("address", address, MatchMode.ANYWHERE));
        }

        if (phoneNumber != null) {
            criteria.add(Restrictions.ilike("phoneNumber", phoneNumber, MatchMode.ANYWHERE));
        }

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public ResponseEntity<List<Customer>> getAllCustomers(Integer pageNumber, Integer pageSize,
                                                          String name, String address, String phoneNumber) {
        List<Customer> retrievedCustomerList = searchCustomerBy(name, address, phoneNumber);
        return PagingUtility.getAll(retrievedCustomerList, pageSize, pageNumber);
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

    public ResponseEntity<List<Customer>> getCustomerByAttribute(String attributeName, String attributeValue, int pageSize, int pageNum) {
        if (attributeValue == null || attributeValue.isEmpty()) return null;
        if (attributeName == null || attributeName.isEmpty()) return null;
        if (!availableAttribute.contains(attributeName)) return null;

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Customer.class);
        criteria.add(Restrictions.like(attributeName, attributeValue, MatchMode.ANYWHERE).ignoreCase());
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return criteria.list().isEmpty() ? null : PagingUtility.getAll((List<Customer>) criteria.list(), pageSize, pageNum);
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
