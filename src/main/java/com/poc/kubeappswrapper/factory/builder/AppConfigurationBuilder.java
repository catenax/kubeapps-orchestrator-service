package com.poc.kubeappswrapper.factory.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kubeappswrapper.entity.AppDetails;

import lombok.SneakyThrows;

@Service
public class AppConfigurationBuilder {

	@SneakyThrows
	public String buildConfiguration(AppDetails appDetails, Map<String, String> inputProperties) {

		appDetails.getYamlValueFieldType();

		Map<String, String> expectedConfiguration = new HashMap<>();

		@SuppressWarnings("unchecked")
		Map<String, Object> expectedInputConfiguration = new ObjectMapper().readValue(appDetails.getExpectedInputData(),
				HashMap.class);

		expectedInputConfiguration.forEach((key, value) -> {

			if (inputProperties.containsKey(value)) {
				String stringValue = inputProperties.get(value);
				expectedConfiguration.put(key, stringValue);
			} else
				expectedConfiguration.put(key, value.toString());
		});

		StringBuffer sb = new StringBuffer();
		Map<String, Object> dyanamicYamlValues = new HashMap<>();
		if ("JSON".equals(appDetails.getYamlValueFieldType())) {
			String str=new JSONObject(expectedConfiguration).toString();
			dyanamicYamlValues.put("yamlValues", str);

		} else {
			expectedConfiguration.forEach((key, value) -> {
				sb.append(key + "=" + value + "\\n");
			});
			dyanamicYamlValues.put("yamlValues", sb.toString());
		}

		dyanamicYamlValues.put("dnsName", inputProperties.get("dnsName"));


		// Initialize StringSubstitutor instance with value map
		StringSubstitutor stringSubstitutor = new StringSubstitutor(dyanamicYamlValues);

		// replace value map to template string
		return stringSubstitutor.replace(appDetails.getRequiredYamlConfiguration());
	}

}
