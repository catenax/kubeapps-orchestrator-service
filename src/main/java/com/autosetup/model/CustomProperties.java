package com.autosetup.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomProperties {

	@NotBlank(message = "bpnNumber is mandatory")
	@Pattern(regexp = "[a-zA-Z0-9\\_\\-]+",
    message = "bpnNumber should not contains special characters")
	private String bpnNumber;
	
	@NotBlank(message = "SubscriptionId is mandatory")
	@Pattern(regexp = "[a-zA-Z0-9\\_\\-]+",
    message = "SubscriptionId should not contains special characters")
	private String subscriptionId;
	
	@NotBlank(message = "ServiceId is mandatory")
	@Pattern(regexp = "[a-zA-Z0-9\\_\\-]+",
    message = "ServiceId should not contains special characters")
	private String serviceId;
	
	
	private String role;
}
