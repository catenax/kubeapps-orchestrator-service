package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

public class EDCControlPlaneBuilder  implements AppServiceBuilder{

	 Map<String, String> expectedConfiguration = new TreeMap<>();

	 Map<String, String> expectedInputConfiguration = new TreeMap<>();

	public EDCControlPlaneBuilder() {

		expectedConfiguration.put("edc.receiver.http.endpoint", "http://localhost:10092/edc-backend/api/v1/public");
		expectedConfiguration.put("edc.ids.title", "Eclipse Dataspace Connector");
		expectedConfiguration.put("edc.ids.description", "Eclipse Dataspace Connector");
		expectedConfiguration.put("edc.ids.id", "urn:connector:edc");
		expectedConfiguration.put("edc.ids.security.profile", "base");
		expectedConfiguration.put("edc.ids.endpoint", "http://localhost:8282/api/v1/ids");
		expectedConfiguration.put("edc.ids.maintainer", "http://localhost");
		expectedConfiguration.put("edc.ids.curator", "http://localhost");
		expectedConfiguration.put("edc.ids.catalog.id", "urn:catalog:default");
		expectedConfiguration.put("ids.webhook.address", "http://localhost:8282");
		
		expectedConfiguration.put("edc.api.control.auth.apikey.key", "X-Api-Key");
		expectedConfiguration.put("edc.api.control.auth.apikey.value", "password");
		expectedConfiguration.put("edc.api.auth.key", "password");
		expectedConfiguration.put("edc.hostname", "localhost");

		expectedConfiguration.put("edc.oauth.provider.audience", "idsc:IDS_CONNECTORS_ALL");
		expectedInputConfiguration.put("edc.oauth.token.url", "dapsurl");
		expectedInputConfiguration.put("edc.oauth.client.id", "dapsclientid");
		expectedInputConfiguration.put("edc.oauth.provider.jwks.url", "dapsjsksurl");
		
		expectedInputConfiguration.put("edc.vault.hashicorp.url", "vaulturl");
		expectedInputConfiguration.put("edc.vault.hashicorp.token", "vaulttoken");
		expectedInputConfiguration.put("edc.vault.hashicorp.timeout.seconds", "vaulttimeout");

		expectedInputConfiguration.put("edc.transfer.dataplane.token.signer.privatekey.alias", "certificate-private-key");
//		expectedConfiguration.put("edc.transfer.proxy.token.verifier.publickey.alias", "public-key");
//
//		expectedConfiguration.put("edc.public.key.alias", "public-key");
		expectedInputConfiguration.put("edc.transfer.proxy.token.signer.privatekey.alias", "certificate-private-key");

		expectedInputConfiguration.put("edc.oauth.public.key.alias", "daps-cert");
		expectedInputConfiguration.put("edc.oauth.private.key.alias", "certificate-private-key");
//
//		expectedConfiguration.put("edc.transfer.proxy.endpoint", "");
//		expectedConfiguration.put("edc.transfer.dataplane.sync.endpoint", "");

		expectedConfiguration.put("edc.datasource.asset.name", "asset");
		expectedInputConfiguration.put("edc.datasource.asset.url", "edcdatabaseurl");
		expectedInputConfiguration.put("edc.datasource.asset.user", "username");
		expectedInputConfiguration.put("edc.datasource.asset.password", "password");

		expectedConfiguration.put("edc.datasource.contractdefinition.name", "contractdefinition");
		expectedInputConfiguration.put("edc.datasource.contractdefinition.url", "edcdatabaseurl");
		expectedInputConfiguration.put("edc.datasource.contractdefinition.user", "username");
		expectedInputConfiguration.put("edc.datasource.contractdefinition.password", "password");

		expectedConfiguration.put("edc.datasource.contractnegotiation.name", "contractnegotiation");
		expectedInputConfiguration.put("edc.datasource.contractnegotiation.url", "edcdatabaseurl");
		expectedInputConfiguration.put("edc.datasource.contractnegotiation.user", "username");
		expectedInputConfiguration.put("edc.datasource.contractnegotiation.password", "password");

		expectedConfiguration.put("edc.datasource.policy.name", "policy");
		expectedInputConfiguration.put("edc.datasource.policy.url", "edcdatabaseurl");
		expectedInputConfiguration.put("edc.datasource.policy.user", "username");
		expectedInputConfiguration.put("edc.datasource.policy.password", "password");

		expectedConfiguration.put("edc.datasource.transferprocess.name", "transferprocess");
		expectedInputConfiguration.put("edc.datasource.transferprocess.url", "edcdatabaseurl");
		expectedInputConfiguration.put("edc.datasource.transferprocess.user", "username");
		expectedInputConfiguration.put("edc.datasource.transferprocess.password", "password");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		expectedInputConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});
		
//		expectedConfiguration.put("edc.transfer.proxy.endpoint", "http://" + tenantName + "edc-dataplane:9191/public");
//		expectedConfiguration.put("edc.transfer.dataplane.sync.endpoint", "http://"+tenantName+"edc-dataplane:9191/public");
		
		StringBuffer sb=new StringBuffer();
		expectedConfiguration.forEach((key, value) -> {
			sb.append(key+"="+value+"\\n");
		});
		
//		String dynamicValues = "{\"configuration\": {\"properties\":\"" + sb.toString() + "\"}}";
		String dynamicValues = "{\"image\":{\"repository\": \"edc-controlplane-postgresql-hashicorp-vault\","
				+ "\"tag\": \"latest\",\"pullPolicy\": \"IfNotPresent\",\"debug\": false},"
				+ "\"configuration\": {\"properties\":\"" + sb.toString() + "\"}}";
		return dynamicValues;
	}

}
