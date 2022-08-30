package com.poc.kubeappswrapper.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.AttributeObj;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.model.DAPsClientCertificateRequest;
import com.poc.kubeappswrapper.model.DAPsClientRequest;
import com.poc.kubeappswrapper.model.DAPsTokenResponse;
import com.poc.kubeappswrapper.proxy.daps.DAPsAppManageProxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DAPsManager {

	private final DAPsAppManageProxy dapsAppManageProxy;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Value("${daps.clientid}")
	private String clientId;

	@Value("${daps.clientsecret}")
	private String clientSecret;

	@Value("${daps.url}")
	private String dapsurl;

	@Value("${daps.jskurl}")
	private String dapsjsksurl;

	private int counter=0;
	
	public void deleteClientfromDAPs(String connectorclientId) {

		DAPsTokenResponse reponse = dapsAppManageProxy.readAuthToken("client_credentials", clientId, clientSecret,
				"omejdn:admin");

		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", "Bearer " + reponse.getAccess_token());

		dapsAppManageProxy.deleteClient(connectorclientId, requestHeader);

	}

	@Retryable(value = { ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> registerClientInDAPs(CustomerDetails customerDetails, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString())
				.step("DAPS")
				.triggerIdforinsert(triger.getTriggerId())
				.build();
		try {
			String tenantName = customerDetails.getOrganizationName();
			log.info(tenantName + "- DAPS ceating");
			
			String bpnNumber = customerDetails.getBpnNumber();
			String role = customerDetails.getRole();
			String connectorclientId = inputData.get("dapsclientid");

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

			DAPsClientRequest dapsClientRequest = DAPsClientRequest.builder().client_id(connectorclientId)
					.name(tenantName).token_endpoint_auth_method("private_key_jwt").scope(scope)
					.grant_types(grant_types).attributes(attributes).build();

			Map<String, String> requestHeader = new HashMap<>();
			requestHeader.put("Authorization", "Bearer " + reponse.getAccess_token());

			dapsAppManageProxy.createClient(dapsClientRequest, requestHeader);

			String certificateAsString = inputData.get("selfsigncertificate");
			DAPsClientCertificateRequest dapsClientCertificate = DAPsClientCertificateRequest.builder()
					.certificate(certificateAsString).build();

			dapsAppManageProxy.uploadClientCertificate(connectorclientId, dapsClientCertificate, requestHeader);

			inputData.put("dapsurl", dapsurl);
			inputData.put("dapsjsksurl", dapsjsksurl);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(tenantName + "- DAPS created");

		} catch (Exception ex) {

			counter++;
			log.info("DapsManager failed retry attempt: "+counter);
			
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DapsManager Oops! We have an exception - " + ex.getMessage());

		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}
		return inputData;
	}

}
