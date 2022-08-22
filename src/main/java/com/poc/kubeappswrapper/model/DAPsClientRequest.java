package com.poc.kubeappswrapper.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DAPsClientRequest {

	private String client_id;
	private String name;
	private String token_endpoint_auth_method;
	private List<String> scope;
	private List<String> grant_types;
	private List<AttributeObj> attributes;
	
}
