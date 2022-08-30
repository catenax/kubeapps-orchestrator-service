package com.poc.kubeappswrapper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetails {

	private String organizationName;

	@JsonIgnore
	private String tenantName;

	private String organizationUnitName;

	private String email;

	private String contactNumber;

	private String tanNumber;

	private String registrationNumber;

	private String bpnNumber;

	private String role;

	private String country;

	private String state;

	private String city;

}
