package com.poc.kubeappswrapper.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VaultSecreteRequest {

	private Map<String, String> data;

}
