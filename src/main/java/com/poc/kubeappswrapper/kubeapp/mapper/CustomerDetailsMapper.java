package com.poc.kubeappswrapper.kubeapp.mapper;

import org.mapstruct.Mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.SneakyThrows;

@Mapper(componentModel = "spring")
public abstract class CustomerDetailsMapper {

	@SneakyThrows
	public String fromCustomer(CustomerDetails customer) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(customer);
	}

	@SneakyThrows
	public CustomerDetails fromCustomerStr(String customerStr) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(customerStr, CustomerDetails.class);
	}
	
}
