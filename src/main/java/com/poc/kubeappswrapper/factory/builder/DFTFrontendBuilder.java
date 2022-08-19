package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

public class DFTFrontendBuilder implements AppServiceBuilder {

	Map<String, String> expectedConfiguration = new TreeMap<>();
	Map<String, String> expectedInputConfiguration = new TreeMap<>();

	public DFTFrontendBuilder() {

		expectedInputConfiguration.put("REACT_APP_API_URL", "dftbackendurl");
		expectedInputConfiguration.put("REACT_APP_API_KEY", "dftbackendapikey");
		expectedInputConfiguration.put("REACT_APP_API_KEYCLOCK_URL", "dftkeyclockurl");
		expectedInputConfiguration.put("REACT_APP_API_KEYCLOCK_REALM", "dftkeyclockrealm");
		expectedInputConfiguration.put("REACT_APP_API_KEYCLOCK_CLIENTID", "dftkeyclockclientid");

		expectedConfiguration.put("REACT_APP_FILESIZE", "268435456");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		expectedInputConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});

		StringBuffer sb=new StringBuffer();
		expectedConfiguration.forEach((key, value) -> {
			sb.append(key+"="+value+"\\n");
		});
		
//		String dynamicValues = "{\"configuration\": " + "{\"properties\":\"" + sb.toString() + "\"}}";
		
		String dynamicValues = "{\"portContainer\": \"8080\",\"image\":{\"repository\": \"dftfrontend\","
				+ "\"tag\": \"1.0.0\",\"pullPolicy\": \"IfNotPresent\",\"debug\": false},"
				+ "\"configuration\": {\"properties\":\"" + sb.toString() + "\"}}";
		return dynamicValues;
	}
}
