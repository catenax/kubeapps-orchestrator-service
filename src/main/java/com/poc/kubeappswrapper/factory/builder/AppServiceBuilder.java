package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;

public interface AppServiceBuilder {

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties);
}
