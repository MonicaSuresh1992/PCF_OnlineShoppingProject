package com.online.customer.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.online.customer.domain.Customer;
import com.online.customer.repository.CustomerRepository;

public class CustomerService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	CustomerRepository customerRepository;
	private final RabbitTemplate template;

	@Autowired
	public CustomerService(RabbitTemplate template){
		this.template = template;
	}

	
	public List<Customer> getAllCustomers(){
		logger.debug("<<<<<Inside Get All Customers>>>>>>>");
		return customerRepository.getAllCustomers();
		
	}

	public Customer save(Customer customer) {
		logger.debug("<<<<<<<<<Inside Service Save function BEFORE REPOSITORY>>>>>>>>>>");
		customerRepository.save(customer);
		logger.debug("<<<<<<<<<Inside Service Save function AFTER REPOSITORY>>>>>>>>>>");
		logger.debug("<<<<<<<<<Inside Service Save function BEFORE Convert and send>>>>>>>>>>");
		template.convertAndSend(customer);
		logger.debug("<<<<<<<<<Inside Service Save function AFTER Convert and send>>>>>>>>>>");
		return customer;

	}

}
