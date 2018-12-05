package com.online.customer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.online.customer.domain.Customer;
import com.online.customer.service.CustomerService;

@RefreshScope
@RestController
public class CustomerController {
	
	Logger logger = LoggerFactory.getLogger(CustomerController.class); 

	@Autowired
	CustomerService customerService;

	@GetMapping("/customers")
	public List<Customer> getAllCustomers(){
		
		logger.debug("<<<<<<<<<<Get All Customer Details>>>>>>>>>>");
		return customerService.getAllCustomers();

	}
	@PostMapping("/customer")
	 public ResponseEntity<?> add(@RequestBody Customer customer) {
		logger.debug("<<<<<<<<<Before Inserting Customer Details>>>>>>>>>>");
        Customer cust = customerService.save(customer);
        logger.debug("<<<<<<<<<After Inserting Customer Details>>>>>>>>>>");
        assert cust != null;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/" +cust.getId())
                .buildAndExpand().toUri());

        return new ResponseEntity<>(cust, httpHeaders, HttpStatus.CREATED);
    }
	
	}
