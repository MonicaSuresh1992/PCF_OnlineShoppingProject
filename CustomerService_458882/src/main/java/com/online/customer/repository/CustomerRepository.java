package com.online.customer.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.online.customer.domain.Customer;

@Repository
public class CustomerRepository {


	private final JdbcTemplate jdbcTemplate;

	private final String GET_ALL_CUST = "Select * from Customer_458882";
	private final String INSERT_CUST = "insert into Customer_458882(id,email,first_name,last_name) values(?,?,?,?)";
	
	@Autowired
	public CustomerRepository(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}



	private final RowMapper<Customer> rowMapper = (ResultSet rs, int row) -> {
		Customer customer = new Customer();
		customer.setId(rs.getInt("id"));
		customer.setEmail(rs.getString("email"));
		customer.setFirstName(rs.getString("first_name"));
		customer.setLastName(rs.getString("last_name"));
		return customer;
	};

	public List<Customer> getAllCustomers() {


		return this.jdbcTemplate.query(GET_ALL_CUST, rowMapper);
	}

	public Customer save(Customer customer) {
		assert customer.getId() != null;
        assert customer.getEmail()!= null;
        assert customer.getFirstName()!= null;
        assert customer.getLastName()!= null;

        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_CUST);
            ps.setInt(1, customer.getId());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getFirstName());
            ps.setString(4, customer.getLastName());	
            return ps;
        });
        
        return customer;
	}

}

