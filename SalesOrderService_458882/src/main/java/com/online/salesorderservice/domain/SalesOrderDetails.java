package com.online.salesorderservice.domain;

import java.util.ArrayList;
import java.util.List;

public class SalesOrderDetails {

	private String orderDescription;
	private String orderDate;
	private Integer custId;
	private List <Item> itemNameList= new ArrayList<Item>();
	private String WrongData;

	public SalesOrderDetails() {}

	public SalesOrderDetails(String orderDesc, String orderDate, Integer custId, List<Item> itemNameList) {
		super();
		this.orderDescription = orderDesc;
		this.orderDate = orderDate;
		this.custId = custId;
		this.itemNameList = itemNameList;
	}

	public String getOrderDescription() {
		return orderDescription;
	}
	public void setOrderDescription(String orderDescription) {
		this.orderDescription = orderDescription;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public Integer getCustId() {
		return custId;
	}
	public void setCustId(Integer custId) {
		this.custId = custId;
	}
	public List<Item> getItemNameList() {
		return itemNameList;
	}

	public void setItemNameList(List<Item> itemNameList) {
		this.itemNameList = itemNameList;
	}

	public String getWrongData() {
		return WrongData;
	}

	public void setWrongData(String wrongData) {
		WrongData = wrongData;
	}



}
