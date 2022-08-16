package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

@Component
public class EDCDataPlaneBuilder {

	Map<String, String> expectedConfiguration = new TreeMap<>();
	Map<String, String> expectedVaultConfiguration = new TreeMap<>();

	public EDCDataPlaneBuilder() {

		expectedConfiguration.put("edc.hostname", "localhost");

		expectedVaultConfiguration.put("edc.vault.hashicorp.url", "vaulturl");
		expectedVaultConfiguration.put("edc.vault.hashicorp.token", "vaulttoken");
		expectedVaultConfiguration.put("edc.vault.hashicorp.timeout.seconds", "vaulttimeout");

		expectedConfiguration.put("edc.controlplane.validation-endpoint", "");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		// Vault configuration
		expectedVaultConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});


		expectedConfiguration.put("edc.controlplane.validation-endpoint",
				"http://" + tenantName + "edc-controlplane:8182/validation/token");

		StringBuffer sb=new StringBuffer();
		expectedConfiguration.forEach((key, value) -> {
			sb.append(key+"="+value+"\\n");
		});
		
		String edcDataplane = "{\"configuration\": " + "{\"properties\":\"" + sb.toString() + "\"}}";
		return edcDataplane;
	}

}
