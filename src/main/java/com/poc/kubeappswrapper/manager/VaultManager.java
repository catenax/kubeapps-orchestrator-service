package com.poc.kubeappswrapper.manager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
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
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.model.VaultSecreteRequest;
import com.poc.kubeappswrapper.proxy.vault.VaultAppManageProxy;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultManager {

	private final VaultAppManageProxy vaultManagerProxy;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Value("${vault.url}")
	private String valutURL;

	@Value("${vault.token}")
	private String vaulttoken;

	@Value("${vault.timeout}")
	private String vaulttimeout;

	private int counter = 0;

	@SneakyThrows
	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> uploadKeyandValues(CustomerDetails customerDetails, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("VAULT").triggerIdforinsert(triger.getTriggerId()).build();

		try {

			String tenantName = customerDetails.getTenantName();

			Map<String, String> tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put("content", inputData.get("selfsigncertificate"));
			uploadSecrete(tenantName, "daps-cert", tenantVaultSecret);

			tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put("content", inputData.get("selfsigncertificateprivatekey"));
			uploadSecrete(tenantName, "certificate-private-key", tenantVaultSecret);

			inputData.remove("selfsigncertificateprivatekey");
			inputData.remove("selfsigncertificate");

			inputData.put("daps-cert", "daps-cert");
			inputData.put("certificate-private-key", "certificate-private-key");
			inputData.put("valuttenantpath", "/v1/secret/data/" + tenantName);
			inputData.put("vaulturl", valutURL);
			inputData.put("vaulttoken", vaulttoken);
			inputData.put("vaulttimeout", vaulttimeout);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			counter++;
			log.info("VaultManager failed retry attempt: " + counter);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("VaultManager Oops! We have an exception - " + ex.getMessage());

		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return inputData;
	}

	public void uploadSecrete(String tenantName, String secretePath, Map<String, String> tenantVaultSecret)
			throws URISyntaxException {

		String valutURLwithpath = valutURL + "/v1/secret/data/" + tenantName + "/data/" + secretePath;
		VaultSecreteRequest vaultSecreteRequest = VaultSecreteRequest.builder().data(tenantVaultSecret).build();
		URI url = new URI(valutURLwithpath);
		log.info(tenantName + "- Vault secrete created");
		vaultManagerProxy.uploadKeyandValue(url, vaultSecreteRequest);

	}

}
