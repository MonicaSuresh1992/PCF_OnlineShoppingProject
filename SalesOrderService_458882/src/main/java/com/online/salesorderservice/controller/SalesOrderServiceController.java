package com.online.salesorderservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.online.salesorderservice.domain.Customer;
import com.online.salesorderservice.domain.Item;
import com.online.salesorderservice.domain.SalesOrderDetails;
import com.online.salesorderservice.entiry.Customer_SOS_458882;
import com.online.salesorderservice.entiry.Order_Line_Item_458882;
import com.online.salesorderservice.entiry.Sales_Order_458882;
import com.online.salesorderservice.repository.CustomerSOSRepository;
import com.online.salesorderservice.repository.OrderLineItemRepository;
import com.online.salesorderservice.repository.SalesOrderServiceRepository;

@RestController
public class SalesOrderServiceController {

	Logger logger = LoggerFactory.getLogger(SalesOrderServiceController.class); 
	
	@Autowired
	private EurekaClient discoveryClient;

	@Autowired
	CustomerSOSRepository customerSOSRepository;

	@Autowired
	OrderLineItemRepository orderLineItemRepository;

	@Autowired
	SalesOrderServiceRepository salesOrderServiceRepository;

	@Autowired
	private LoadBalancerClient loadBalancerClient;

	@PostMapping("/orders")
	@HystrixCommand(fallbackMethod = "customerOrItemFallBack")
	public String add(@RequestBody SalesOrderDetails salesOrderDetails) throws Exception {
		logger.debug("<<<<<<<<<<Before Validating Customer>>>>>>>>>>");

		//Validating customer
		boolean custExist = false;
		List<Customer_SOS_458882> customerSOSList = customerSOSRepository.findAll();
		if (customerSOSList != null && customerSOSList.size() > 0){

			for (int j=0 ; j < customerSOSList.size() ; j++) {
				System.out.println("customerSOSList.get(j).getCustId()" + customerSOSList.get(j).getCustId());
				System.out.println("salesOrderDetails.getCustId()" + salesOrderDetails.getCustId());
				if (customerSOSList.get(j).getCustId() != null && salesOrderDetails.getCustId() != null){
					if (customerSOSList.get(j).getCustId().equals(salesOrderDetails.getCustId())) {
						custExist = true;
					}
				} else {
					//Customer not available -- Exception to be thrown
					logger.debug("<<<<<<<<<Customer not available-1>>>>");
					salesOrderDetails.setWrongData("WrongCustId");
					throw new Exception();
				}

			}

			if (!custExist){
				logger.debug("<<<<<<<<<Customer not available-2>>>>");
				salesOrderDetails.setWrongData("WrongCustId");
				throw new Exception();
			}
		}	else {
			salesOrderDetails.setWrongData("WrongCustId");
			throw new Exception();
		}

		logger.debug("<<<<<<<<<<After Validating Customer>>>>>>>>>>");

		// REST call to validate items by calling item service with item name -- itemByName ---Starts 
		logger.debug("Before Fetching Items >>>>");		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Item> response = null;

		String itemName = "";
		double totalPrice = 0.0;
		Long orderId = 0L;
		List <Item> fetchedItemList = null;

		if (salesOrderDetails.getItemNameList() != null && salesOrderDetails.getItemNameList().size() >0 ) {
			fetchedItemList = new ArrayList<Item>();
			Item item = null;
			for (int i=0 ; i < salesOrderDetails.getItemNameList().size() ; i++) {
				item = new Item();
				itemName = salesOrderDetails.getItemNameList().get(i).getItemName();
				item.setItemName(itemName);
				System.out.println("<<<<<<<<<Item Name >>>"+itemName);
				response = restTemplate.getForEntity(fetchItemServiceUrl() + "/items/"+itemName, Item.class);
				logger.debug("<<<<<<<After Fetching Items >>>>>>>");

				System.out.println("Response"+response.getBody());
				itemName = response.getBody().getItemName();
				totalPrice = totalPrice + response.getBody().getItemPrice();
				fetchedItemList.add(item);
				if(itemName.equals(null) || itemName.equals("")){
					//Item details not available -- Exception to be thrown
					logger.debug("<<<<<<<<<Item details not available-1>>>");
					salesOrderDetails.setWrongData("WrongItemName");
				}
			}
		} else {
			logger.debug("<<<<<<<<<Item details not available-2>>>");
			salesOrderDetails.setWrongData("WrongItemName");
			//Item details not available -- Exception to be thrown
		}

		// REST call to validate items by calling item service with item name -- itemByName --- Ends

		// create order by inserting the order details in sales_order table --- Starts
		if(salesOrderDetails != null ) {

			Sales_Order_458882 salesOrder = new Sales_Order_458882();
			if (salesOrderDetails.getOrderDate() != null ) {
				salesOrder.setOrderDate(salesOrderDetails.getOrderDate());
			}
			if (salesOrderDetails.getCustId() != null ) {
				salesOrder.setCustId(salesOrderDetails.getCustId() );
			}
			if (salesOrderDetails.getOrderDescription() != null ) {
				salesOrder.setOrderDesc(salesOrderDetails.getOrderDescription() );
			}
			if (totalPrice != 0.0 ) {
				salesOrder.setTotalPrice(totalPrice);
			}

			salesOrder = salesOrderServiceRepository.save(salesOrder);
			orderId = salesOrder.getOrderId();
			// create order by inserting the order details in sales_order table --- Ends

			assert orderId != null;

			// create order line by inserting the order details in order_line_item table --- Starts
			if (fetchedItemList != null && fetchedItemList.size() >0 ) {
				for (int i=0 ; i < fetchedItemList.size() ; i++) {
					if (fetchedItemList.get(i).getItemName() != null && fetchedItemList.get(i).getItemName() != ""){
						Order_Line_Item_458882 orderLineItem = new Order_Line_Item_458882();
						orderLineItem.setItemName(fetchedItemList.get(i).getItemName());
						orderLineItem.setItemQuantity(fetchedItemList.size());// Check
						orderLineItem.setOrderId(orderId);

						orderLineItemRepository.save(orderLineItem);

					} else {
						//Item details not available -- Exception to be thrown
						logger.debug("<<<<<<<<<Item details not available 1 >>>");
						salesOrderDetails.setWrongData("WrongItemName");
						throw new Exception();
					}
				}
			}

			else {
				//Item details not available -- Exception to be thrown
				System.out.println("<<<<<<<<<Item details not available 2 >>>");
				salesOrderDetails.setWrongData("WrongItemName");
				throw new Exception();
			}
			// create order line by inserting the order details in order_line_item table --- Ends
		}
		System.out.println("<<<<<<<<<Order Id >>>"+orderId.toString());
		//		return orderId.toString();

		//        HttpHeaders httpHeaders = new HttpHeaders();
		//        httpHeaders.setLocation(ServletUriComponentsBuilder
		//                .fromCurrentRequest().path("/" +orderId)
		//                .buildAndExpand().toUri());

		return orderId.toString();

	}

	public void insertCustomerSOS(Customer customer) {
		//System.out.println("<<<<<<<<<<<<<<<<<<<<<<<insertCustomerSOS>>>>>>>>>>>>>>>>>>>>>");
		Customer_SOS_458882 customerSOS  = new Customer_SOS_458882();

		customerSOS.setCustId(customer.getId());
		customerSOS.setCustFirstName(customer.getFirstName());
		customerSOS.setCustLastName(customer.getLastName());
		customerSOS.setCustEmail(customer.getEmail());

		customerSOSRepository.save(customerSOS);
	}

	// This method is for implementing Ribbon - Client side load balancing
//	private String fetchItemServiceUrl() {
//
//		//System.out.println("Inside fetchItemServiceUrl");
//
//
//		ServiceInstance instance = loadBalancerClient.choose("item-service_458882");
//
//		//System.out.println("After fetching instance in fetchItemServiceUrl");
//		//System.out.println("uri: {}"+ instance.getUri().toString());
//		//System.out.println("serviceId: {}"+ instance.getServiceId());
//
//		return instance.getUri().toString();
//	}
	
	private String fetchItemServiceUrl() {

		System.out.println("Inside fetchItemServiceUrl");


		InstanceInfo instance = discoveryClient.getNextServerFromEureka("item-service_458882",false);

		System.out.println("After fetching instance in fetchItemServiceUrl");
		System.out.println("uri: {}"+ instance.getHomePageUrl());
		System.out.println("serviceId: {}"+ instance.getId());

		return instance.getHomePageUrl();
	}

	public String customerOrItemFallBack(SalesOrderDetails salesOrderDetails){
		if (salesOrderDetails.getWrongData()!=null && salesOrderDetails.getWrongData().equalsIgnoreCase("WrongCustId")){
			return "Customer Id is not valid. Please enter the correct Id";
		} else {
			return "Item Name is not valid. Please enter the correct Item";
		}
		
	}
}

