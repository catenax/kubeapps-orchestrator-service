package com.autosetup.model;

import java.util.Map;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

	@NotBlank(message = "OrganizationName is mandatory")
	private String organizationName;

	private String organizationUnitName;

	@NotBlank(message = "Email is mandatory")
	@Email(message = "Email is not correct format")
	private String email;

	private String contactNumber;

	private String tanNumber;
	
	private Map<String,String> properties;

	private String registrationNumber;

	@NotBlank(message = "Country is mandatory")
	private String country;

	private String state;

	private String city;

}
