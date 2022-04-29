package cosc2440.asm2.taxi_company.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.repository.CustomerRepository;
import cosc2440.asm2.taxi_company.service.CustomerService;
import org.junit.Before;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

//    private List<Customer> setUpData(){
//        return Arrays.asList(new Customer(1,"An Bui", "0123456789", "tphcm"),
//                new Customer(2,"Tuan Dao", "9876543210", "tphcm"),
//                new Customer(3,"Bao Nguyen", "0246802468", "tphcm"),
//                new Customer(4,"Long Nguyen", "1357913579", "tphcm"));
//    }

    @Test
    void addCustomer() throws Exception {
        Customer customer = new Customer(1L,"Dao Kha Tuan", "22222", "tphcm");
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        assertEquals("Customer with id 1 added successfully!", customerService.addCustomer(customer));

        Customer customer2 = new Customer(2L,"new", "9999", "tphcm");
        Mockito.when(customerRepository.save(customer2)).thenReturn(customer2);
        assertEquals("Customer with id 2 added successfully!", customerController.addCustomer(customer2));

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        MvcResult mvcResult =mockMvc.perform(MockMvcRequestBuilders.post("/customer").contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(customer))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String stringResult = mvcResult.getResponse().getContentAsString();
        assertEquals("Customer with id 1 added successfully!", stringResult);
    }
}