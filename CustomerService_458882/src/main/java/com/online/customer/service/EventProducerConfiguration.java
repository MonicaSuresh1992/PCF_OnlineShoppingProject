package com.online.customer.service;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EventProducerConfiguration {

	private final ConnectionFactory connectionFactory;

	 @Bean
	 public CustomerService customerService(RabbitTemplate rabbitTemplate) {
	   return new CustomerService(rabbitTemplate);
	 }
	 
	@Autowired
	public EventProducerConfiguration(ConnectionFactory connectionFactory){
		this.connectionFactory = connectionFactory;
	}

	@Bean
	public RabbitTemplate template() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(this.connectionFactory);
		rabbitTemplate.setRoutingKey("CustomerCreated");
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		return rabbitTemplate;
	}

}