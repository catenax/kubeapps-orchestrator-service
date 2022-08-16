package com.poc.kubeappswrapper.model;

import lombok.Data;

@Data
public class DAPsTokenResponse {

	private String access_token;
	private Integer expires_in;
	private String token_type;
	private String scope;

}
