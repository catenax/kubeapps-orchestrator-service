package com.autosetup.wrapper.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePackageRequest {
	
	private String pluginName;
	private String pluginVersion;
	private String contextCluster;
	private String contextNamespace;
	private String targetCluster;
	private String targetNamespace;
	private String availablePackageIdentifier;
	private String availablePackageVersion;
	private String values;
	

}
