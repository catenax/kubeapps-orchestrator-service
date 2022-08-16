package com.poc.kubeappswrapper.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.model.AttributeObj;
import com.poc.kubeappswrapper.model.DAPsClientCertificateRequest;
import com.poc.kubeappswrapper.model.DAPsClientRequest;
import com.poc.kubeappswrapper.model.DAPsTokenResponse;
import com.poc.kubeappswrapper.proxy.daps.DAPsAppManageProxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DAPsManager {

	private final DAPsAppManageProxy dapsAppManageProxy;
	private final CertificateManager certificateManager; 

	@Value("${daps.clientid}")
	private String clientId;

	@Value("${daps.clientsecret}")
	private String clientSecret;

	@Value("${daps.url}")
	private String dapsurl;

	
	@Value("${daps.jskurl}")
	private String dapsjsksurl;
	
	public Map<String, String> registerClientInDAPs(String connectorclientId, String tenantName, String bpnNumber, String role) {

		DAPsTokenResponse reponse = dapsAppManageProxy.readAuthToken("client_credentials", clientId, clientSecret,
				"omejdn:admin");

		List<String> scope = new ArrayList<>();
		scope.add("idsc:IDS_CONNECTOR_ATTRIBUTES_ALL");

		List<String> grant_types = new ArrayList<>();
		grant_types.add("client_credentials");

		List<AttributeObj> attributes = new ArrayList<>();
		attributes.add(new AttributeObj("idsc", "IDS_CONNECTOR_ATTRIBUTES_ALL"));
		attributes.add(new AttributeObj("@type", "ids:DatPayload"));
		attributes.add(new AttributeObj("@context", "https://w3id.org/idsa/contexts/context.jsonld"));
		attributes.add(new AttributeObj("securityProfile", "idsc:BASE_SECURITY_PROFILE"));
		attributes.add(new AttributeObj("referringConnector", "http://www." + tenantName + ".com/" + bpnNumber));
		attributes.add(new AttributeObj("role", role));

		DAPsClientRequest dapsClientRequest = DAPsClientRequest.builder()
				.client_id(connectorclientId)
				.name(tenantName)
				.token_endpoint_auth_method("private_key_jwt")
				.scope(scope)
				.grant_types(grant_types)
				.attributes(attributes).build();

		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", "Bearer "+reponse.getAccess_token());
		
		dapsAppManageProxy.createClient(dapsClientRequest, requestHeader);
		
		
		DAPsClientCertificateRequest dapsClientCertificate=DAPsClientCertificateRequest
				.builder()
				.certificate(certificateManager.readCertificate(tenantName))
				.build();
		
		dapsAppManageProxy.uploadClientCertificate(connectorclientId, dapsClientCertificate, requestHeader);
		
		Map<String, String> inputConfiguration = new HashMap<>();
		inputConfiguration.put("dapsurl", dapsurl);
		inputConfiguration.put("dapsjsksurl", dapsjsksurl);
		
		return inputConfiguration;
	}

}
