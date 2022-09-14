package com.autosetup.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.ServiceException;
import com.autosetup.model.Attribute;
import com.autosetup.model.Customer;
import com.autosetup.model.DAPsClientCertificateRequest;
import com.autosetup.model.DAPsClientRequest;
import com.autosetup.model.DAPsTokenResponse;
import com.autosetup.model.SelectedTools;
import com.autosetup.proxy.daps.DAPsAppManageProxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
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
	
	@Value("${daps.token.url}")
	private String dapstokenurl;

	@Value("${daps.jskurl}")
	private String dapsjsksurl;

	public void deleteClientfromDAPs(String connectorclientId) {

		DAPsTokenResponse reponse = dapsAppManageProxy.readAuthToken("client_credentials", clientId, clientSecret,
				"omejdn:admin");

		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", "Bearer " + reponse.getAccess_token());

		dapsAppManageProxy.deleteClient(connectorclientId, requestHeader);

	}

	@Retryable(value = { ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> registerClientInDAPs(Customer customerDetails, SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString())
				.step("DAPS")
				.triggerIdforinsert(triger.getTriggerId())
				.build();
		try {
			String tenantName = customerDetails.getOrganizationName();
			log.info(tenantName + "- DAPS ceating");
			
			String bpnNumber = inputData.get("bpnNumber");
			String role = inputData.get("role");
			String connectorclientId = inputData.get("dapsclientid");

			DAPsTokenResponse reponse = dapsAppManageProxy.readAuthToken("client_credentials", clientId, clientSecret,
					"omejdn:admin");

			List<String> scope = new ArrayList<>();
			scope.add("idsc:IDS_CONNECTOR_ATTRIBUTES_ALL");

			List<String> grant_types = new ArrayList<>();
			grant_types.add("client_credentials");

			List<Attribute> attributes = new ArrayList<>();
			attributes.add(new Attribute("idsc", "IDS_CONNECTOR_ATTRIBUTES_ALL"));
			attributes.add(new Attribute("@type", "ids:DatPayload"));
			attributes.add(new Attribute("@context", "https://w3id.org/idsa/contexts/context.jsonld"));
			attributes.add(new Attribute("securityProfile", "idsc:BASE_SECURITY_PROFILE"));
			attributes.add(new Attribute("referringConnector", "http://www." + tenantName + ".com/" + bpnNumber));
			attributes.add(new Attribute("role", role));

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
			inputData.put("dapstokenurl", dapstokenurl);
			inputData.put("dapsjsksurl", dapsjsksurl);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(tenantName + "- DAPS created");

		} catch (Exception ex) {

			log.error("DapsManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);
			
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DapsManager Oops! We have an exception - " + ex.getMessage());

		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}
		return inputData;
	}

}
