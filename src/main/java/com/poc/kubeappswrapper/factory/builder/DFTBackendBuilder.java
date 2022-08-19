package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

@Component
public class DFTBackendBuilder implements AppServiceBuilder {

	Map<String, String> expectedConfiguration = new TreeMap<>();
	Map<String, String> expectedInputConfiguration = new TreeMap<>();

	public DFTBackendBuilder() {

		expectedConfiguration.put("server.port", "8080");

		// Enable multipart uploads
		expectedConfiguration.put("spring.servlet.multipart.enabled", "true");

		// Threshold after which files are written to disk.
		expectedConfiguration.put("spring.servlet.multipart.file-size-threshold", "2KB");

		// Max file size.
		expectedConfiguration.put("spring.servlet.multipart.max-file-size", "200MB");

		// Max Request Size
		expectedConfiguration.put("spring.servlet.multipart.max-request-size", "215MB");
		expectedConfiguration.put("server.servlet.context-path", "/api");

		// Flyway
		expectedConfiguration.put("spring.flyway.baseline-on-migrate", "true");
		expectedConfiguration.put("spring.flyway.locations", "classpath:/flyway");

		// File Storage Properties"
		expectedConfiguration.put("file.upload-dir", "./temp/");

		// LOGGING"
		expectedConfiguration.put("logging.level.org.apache.http", "info");
		expectedConfiguration.put("logging.level.root", "info");

		// Database
		expectedConfiguration.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
		expectedConfiguration.put("spring.jpa.open-in-view", "false");
		
		expectedInputConfiguration.put("spring.datasource.url", "dftdatabaseurl");
		expectedInputConfiguration.put("spring.datasource.username", "username");
		expectedInputConfiguration.put("spring.datasource.password", "password");

		// Digital Twins
		expectedInputConfiguration.put("digital-twins.hostname", "digital-twins.hostname");
		expectedInputConfiguration.put("digital-twins.authentication.url", "digital-twins.authentication.url");
		expectedInputConfiguration.put("digital-twins.authentication.clientId",
				"digital-twins.authentication.clientId");
		expectedInputConfiguration.put("digital-twins.authentication.clientSecret",
				"digital-twins.authentication.clientSecret");

		// EDC
		expectedConfiguration.put("edc.enabled", "true");
		expectedInputConfiguration.put("edc.hostname", "edc.hostname");
		expectedInputConfiguration.put("edc.apiKeyHeader", "edc.apiKeyHeader");
		expectedInputConfiguration.put("edc.apiKey", "edc.apiKey");

		// DFT configuration for EDC");
		expectedInputConfiguration.put("dft.hostname", "dft.hostname");
		expectedInputConfiguration.put("dft.apiKeyHeader", "dft.apiKeyHeader");
		expectedInputConfiguration.put("dft.apiKey", "dft.apiKey");

		// Manufacturer Id");
		expectedInputConfiguration.put("manufacturerId", "manufacturerId");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		expectedInputConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});

		StringBuffer sb = new StringBuffer();
		expectedConfiguration.forEach((key, value) -> {
			sb.append(key + "=" + value + "\\n");
		});

//		String dynamicValues = "{\"configuration\": " + "{\"properties\":\"" + sb.toString() + "\"}}";
		String dynamicValues = "{\"image\":{\"repository\": \"dft-backend\","
				+ "\"tag\": \"1.0.0\",\"pullPolicy\": \"IfNotPresent\",\"debug\": false},"
				+ "\"configuration\": {\"properties\":\"" + sb.toString() + "\"}}";
		return dynamicValues;
	}
}
