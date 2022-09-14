package com.autosetup.kubeapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Context {

	private String cluster;
	private String namespace;
	
}
