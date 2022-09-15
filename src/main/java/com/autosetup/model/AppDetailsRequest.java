package com.autosetup.model;

import lombok.Data;

@Data
public class AppDetailsRequest {

	private String appName;

	private String contextCluster;

	private String contextNamespace;

	private String packageIdentifier;

	private String pluginName;

	private String pluginVersion;

	private String packageVersion;
	
	private String expectedInputData;
	
	private String outputData;
	
	private String requiredYamlConfiguration;
	
	private String yamlValueFieldType;

}
