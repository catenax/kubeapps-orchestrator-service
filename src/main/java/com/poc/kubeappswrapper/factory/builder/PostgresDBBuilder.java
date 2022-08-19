package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class PostgresDBBuilder  implements AppServiceBuilder {

	private Map<String, String> expectedConfiguration = new TreeMap<>();
	private Map<String, String> expectedInputConfiguration = new TreeMap<>();

	public PostgresDBBuilder() {

		expectedInputConfiguration.put("postgresPassword", "postgresPassword");
		expectedInputConfiguration.put("username", "username");
		expectedInputConfiguration.put("password", "password");
		expectedInputConfiguration.put("database", "database");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		// Update Database configuration
		expectedInputConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});

		JSONObject json = new JSONObject(expectedConfiguration);
		String properties = json.toString();
		String dynamicValues = "{\"global\":{\"postgresql\":{\"auth\":" + properties + "}}}";
		return dynamicValues;
	}

}
