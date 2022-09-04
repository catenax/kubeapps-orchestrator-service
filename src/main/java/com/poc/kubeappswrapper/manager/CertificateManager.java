package com.poc.kubeappswrapper.manager;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.utility.Certutil;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateManager {

	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@SneakyThrows
	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> createCertificate(CustomerDetails customerDetails, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CERTIFICATE").triggerIdforinsert(triger.getTriggerId()).build();

		try {

			String tenantName = customerDetails.getTenantName();
			log.info(tenantName + "- certificate creating");

			String C = Optional.ofNullable(customerDetails.getCountry()).map(r -> r).orElse("DE");
			String ST = Optional.ofNullable(customerDetails.getState()).map(r -> r).orElse("BE");
			String L = Optional.ofNullable(customerDetails.getCity()).map(r -> r).orElse("Berline");

			String params = String.format("O=%s, OU=%s, C=%s, ST=%s, L=%s, CN=%s", tenantName,
					customerDetails.getBpnNumber(), C, ST, L, "www." + tenantName + ".com");

			Certutil.CertKeyPair certificateDetails = Certutil.generateSelfSignedCertificateSecret(params, null, null);
			X509Certificate certificate = certificateDetails.certificate();
			String clientId = Certutil.getClientId(certificate);

			outputData.put("dapsclientid", clientId);
			outputData.put("selfsigncertificate", Certutil.getAsString(certificate));
			outputData.put("selfsigncertificateprivatekey",
					Certutil.getAsString(certificateDetails.keyPair().getPrivate()));

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(tenantName + "- certificate created");

		} catch (Exception ex) {

			log.error("CertificateManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("CertificateManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return outputData;

	}

}
