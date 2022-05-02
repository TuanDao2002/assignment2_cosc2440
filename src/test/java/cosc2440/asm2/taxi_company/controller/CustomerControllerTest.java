package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.repository.CustomerRepository;
import cosc2440.asm2.taxi_company.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @MockBean
    protected CustomerRepository customerRepository;

    @InjectMocks
    @Autowired
    protected CustomerService customerService;

    @InjectMocks
    @Autowired
    protected CustomerController customerController;

    @Autowired
    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    protected List<Customer> customers = new ArrayList<>();

    @BeforeEach
    void setUp(){
        Customer c1 = new Customer(1L,"An Bui", "0123456789", "tphcm");
        Customer c2 = new Customer(2L,"Tuan Dao", "9876543210", "tphcm");

        customers.add(c1);
        customers.add(c2);

        Customer savedCustomer1 = customerRepository.save(c1);
        Customer savedCustomer2 = customerRepository.save(c2);

        Mockito.when(customerRepository.save(c1)).thenReturn(savedCustomer1);
        Mockito.when(customerRepository.save(c2)).thenReturn(savedCustomer2);
        Mockito.when(customerRepository.findAll()).thenReturn(customers);
    }

    @Test
    void addCustomer() throws Exception {
        Customer customer = new Customer(1L,"Dao Kha Tuan", "22222", "tphcm");
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        assertEquals("Customer with id 1 added successfully!", customerController.addCustomer(customer));

        Customer customer2 = new Customer(2L,"new", "9999", "tphcm");
        Mockito.when(customerRepository.save(customer2)).thenReturn(customer2);
        assertEquals("Customer with id 2 added successfully!", customerController.addCustomer(customer2));

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/admin/customer").contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(customer))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String stringResult = mvcResult.getResponse().getContentAsString();
        assertEquals("Customer with id 1 added successfully!", stringResult);
    }

    @Test
    void getCustomerById() throws Exception {
        Long customerId = 1L;
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customers.get(0)));
        Customer getCustomer = customerController.getCustomerById(customerId);
        assertNotNull(getCustomer);
        assertEquals(customerId, getCustomer.getId());
        assertEquals(customers.get(0), getCustomer);

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/customer" + "/" + getCustomer.getId()).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteCustomerById() throws Exception {
        Long customerId = 1L;
        Long customerIdNotExist = 3L;
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customers.get(0)));
        String result = customerController.deleteCustomerById(customerId);
        Customer customerDoesNotExist = customerController.getCustomerById(customerIdNotExist);

        assertNull(customerDoesNotExist);
        assertEquals("Customer with id 3 does not exist!", customerController.deleteCustomerById(customerIdNotExist));
        assertEquals("Customer with id 1 deleted!", result);

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/customer" + "/" + customerId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getAllCustomer() throws Exception {
        ResponseEntity<List<Customer>> expectedResponse = new ResponseEntity<>(customers, new HttpHeaders(), HttpStatus.OK);
        ResponseEntity<List<Customer>> actualResponse = customerController.getAllCustomers(0, 20, null, null, null);

        assertTrue(Objects.requireNonNull(expectedResponse.getBody()).containsAll(actualResponse.getBody()));

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/customer")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateCustomer() throws Exception {
        Customer customer = new Customer(3L, "Bao Nguyen", "0246802468", "tphcm");
        assertEquals("Customer with id 3 does not exist!", customerController.updateCustomer(customer));
        Mockito.when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        customer.setName("Bao Nguyen");
        customer.setPhoneNumber("0246802468");
        customer.setAddress("soc trang");

        String updatedCustomer = customerController.updateCustomer(customer);
        assertEquals("Customer with id 3 updated!", updatedCustomer);

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/customer")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customer))
                ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getCustomerByAttribute() throws Exception {

        ResponseEntity<List<Customer>> getCustomers1 = customerService.getCustomerByAttribute("name", "An Bui", 20, 0);
        ResponseEntity<List<Customer>> getCustomers2 = customerService.getCustomerByAttribute("address", "tphcm", 20, 0);
        ResponseEntity<List<Customer>> getCustomers3 = customerService.getCustomerByAttribute("phoneNumber", "9876543210", 20, 0);

        assertEquals(1 ,getCustomers1.getBody().size());
        assertEquals(2 ,getCustomers2.getBody().size());
        assertEquals(1 ,getCustomers3.getBody().size());

        assertTrue(getCustomers1.getBody().contains(customers.get(0)));
        assertEquals(customers ,getCustomers2.getBody());
        assertTrue(getCustomers3.getBody().contains(customers.get(1)));

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/customer/attribute?attributeName=name&&attributeValue=AnBui")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(getCustomers1)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/customer/attribute?attributeName=address&&attributeValue=tphcm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(getCustomers2)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/customer/attribute?attributeName=phoneNumber&&attributeValue=9876543210")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(getCustomers3)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}