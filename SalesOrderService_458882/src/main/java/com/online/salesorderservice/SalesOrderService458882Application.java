package com.online.salesorderservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

import com.online.salesorderservice.controller.SalesOrderServiceController;
import com.online.salesorderservice.domain.Customer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
public class SalesOrderService458882Application {

	@Autowired
	private SalesOrderServiceController salesOrderController;
	
	public static void main(String[] args) {
		SpringApplication.run(SalesOrderService458882Application.class, args);
	}
	
	@RabbitListener(queues = "CustomerCreated")
	public void receiveMessage(Customer customer) {
		System.out.println("Customer Details:" + "\nId:"+customer.getId()+"\nEmail:"+customer.getEmail()+"\nFirst Name:"+customer.getFirstName()+"\nLast Name:"+customer.getLastName());
		salesOrderController.insertCustomerSOS(customer);
	}
}
	