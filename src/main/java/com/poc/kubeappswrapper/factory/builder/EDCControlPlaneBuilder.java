package com.poc.kubeappswrapper.factory.builder;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

@Component
public class EDCControlPlaneBuilder {

	 Map<String, String> expectedConfiguration = new TreeMap<>();

	 Map<String, String> expectedDAPsConfiguration = new TreeMap<>();

	 Map<String, String> expectedPgDBConfiguration = new TreeMap<>();

	 Map<String, String> expectedVaultConfiguration = new TreeMap<>();

	 Map<String, String> expectedOAuthConfiguration = new TreeMap<>();

	 Map<String, String> expectedDataPlaneConfiguration = new TreeMap<>();

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
		expectedDAPsConfiguration.put("edc.oauth.token.url", "dapsurl");
		expectedDAPsConfiguration.put("edc.oauth.client.id", "dapsclientid");
		expectedDAPsConfiguration.put("edc.oauth.provider.jwks.url", "dapsjsksurl");
		
		expectedVaultConfiguration.put("edc.vault.hashicorp.url", "vaulturl");
		expectedVaultConfiguration.put("edc.vault.hashicorp.token", "vaulttoken");
		expectedVaultConfiguration.put("edc.vault.hashicorp.timeout.seconds", "vaulttimeout");

		expectedVaultConfiguration.put("edc.transfer.dataplane.token.signer.privatekey.alias", "certificate-private-key");
//		expectedConfiguration.put("edc.transfer.proxy.token.verifier.publickey.alias", "public-key");
//
//		expectedConfiguration.put("edc.public.key.alias", "public-key");
		expectedVaultConfiguration.put("edc.transfer.proxy.token.signer.privatekey.alias", "certificate-private-key");

		expectedVaultConfiguration.put("edc.oauth.public.key.alias", "daps-cert");
		expectedVaultConfiguration.put("edc.oauth.private.key.alias", "certificate-private-key");
//
//		expectedConfiguration.put("edc.transfer.proxy.endpoint", "");
//		expectedConfiguration.put("edc.transfer.dataplane.sync.endpoint", "");

		expectedConfiguration.put("edc.datasource.asset.name", "asset");
		expectedPgDBConfiguration.put("edc.datasource.asset.url", "url");
		expectedPgDBConfiguration.put("edc.datasource.asset.user", "username");
		expectedPgDBConfiguration.put("edc.datasource.asset.password", "password");

		expectedConfiguration.put("edc.datasource.contractdefinition.name", "contractdefinition");
		expectedPgDBConfiguration.put("edc.datasource.contractdefinition.url", "url");
		expectedPgDBConfiguration.put("edc.datasource.contractdefinition.user", "username");
		expectedPgDBConfiguration.put("edc.datasource.contractdefinition.password", "password");

		expectedConfiguration.put("edc.datasource.contractnegotiation.name", "contractnegotiation");
		expectedPgDBConfiguration.put("edc.datasource.contractnegotiation.url", "url");
		expectedPgDBConfiguration.put("edc.datasource.contractnegotiation.user", "username");
		expectedPgDBConfiguration.put("edc.datasource.contractnegotiation.password", "password");

		expectedConfiguration.put("edc.datasource.policy.name", "policy");
		expectedPgDBConfiguration.put("edc.datasource.policy.url", "url");
		expectedPgDBConfiguration.put("edc.datasource.policy.user", "username");
		expectedPgDBConfiguration.put("edc.datasource.policy.password", "password");

		expectedConfiguration.put("edc.datasource.transferprocess.name", "transferprocess");
		expectedPgDBConfiguration.put("edc.datasource.transferprocess.url", "url");
		expectedPgDBConfiguration.put("edc.datasource.transferprocess.user", "username");
		expectedPgDBConfiguration.put("edc.datasource.transferprocess.password", "password");

	}

	public String buildConfiguration(String appName, String tenantName, Map<String, String> inputProperties) {

		String dbName= inputProperties.get("database");
		// Update Database configuration
		expectedPgDBConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			
			if (value.equals("url"))
				stringValue = "jdbc:postgresql://" + tenantName + "postgresdb-postgresql:5432/"+dbName;

			expectedConfiguration.put(key, stringValue);
		});

		// Vault configuration
		expectedVaultConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});
		

		expectedDAPsConfiguration.forEach((key, value) -> {
			String stringValue = inputProperties.get(value);
			expectedConfiguration.put(key, stringValue);
		});

		
		
//		expectedConfiguration.put("edc.transfer.proxy.endpoint", "http://" + tenantName + "edc-dataplane:9191/public");
//		expectedConfiguration.put("edc.transfer.dataplane.sync.endpoint", "http://"+tenantName+"edc-dataplane:9191/public");
		
		StringBuffer sb=new StringBuffer();
		expectedConfiguration.forEach((key, value) -> {
			sb.append(key+"="+value+"\\n");
		});
		
//		String edcControlplane = "{\"configuration\": {\"properties\":\"" + sb.toString() + "\"}}";
		String edcControlplane = "{\"image\":{\"repository\": \"edc-controlplane-postgresql-hashicorp-vault\","
				+ "\"tag\": \"latest\",\"pullPolicy\": \"IfNotPresent\",\"debug\": false},"
				+ "\"configuration\": {\"properties\":\"" + sb.toString() + "\"}}";
		return edcControlplane;
	}

}
