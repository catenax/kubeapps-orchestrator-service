package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class PostgresDBBuilder {

	private Map<String, String> expectedConfiguration = new TreeMap<>();
	private Map<String, String> expectedDBConfiguration = new TreeMap<>();

	public PostgresDBBuilder() {

		expectedDBConfiguration.put("postgresPassword", "postgresPassword");
		expectedDBConfiguration.put("username", "username");
		expectedDBConfiguration.put("password", "password");
		expectedDBConfiguration.put("database", "database");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		// Update Database configuration
		expectedDBConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});

		JSONObject json = new JSONObject(expectedConfiguration);
		String properties = json.toString();
		String configuration = "{\"global\":{\"postgresql\":{\"auth\":" + properties + "}}}";
		return configuration;
	}

}
