package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

public class EDCDataPlaneBuilder  implements AppServiceBuilder {

	Map<String, String> expectedConfiguration = new TreeMap<>();
	Map<String, String> expectedInputConfiguration = new TreeMap<>();

	public EDCDataPlaneBuilder() {

		expectedConfiguration.put("edc.hostname", "localhost");

		expectedInputConfiguration.put("edc.vault.hashicorp.url", "vaulturl");
		expectedInputConfiguration.put("edc.vault.hashicorp.token", "vaulttoken");
		expectedInputConfiguration.put("edc.vault.hashicorp.timeout.seconds", "vaulttimeout");

		expectedConfiguration.put("edc.controlplane.validation-endpoint", "");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		// Vault configuration
		expectedInputConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});


		expectedConfiguration.put("edc.controlplane.validation-endpoint",
				"http://" + tenantName + "edc-controlplane:8182/validation/token");

		StringBuffer sb=new StringBuffer();
		expectedConfiguration.forEach((key, value) -> {
			sb.append(key+"="+value+"\\n");
		});
		
		String dynamicValues = "{\"configuration\": " + "{\"properties\":\"" + sb.toString() + "\"}}";
		return dynamicValues;
	}

}
