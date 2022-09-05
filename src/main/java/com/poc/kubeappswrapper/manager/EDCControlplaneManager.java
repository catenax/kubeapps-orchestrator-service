package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.utility.PasswordGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EDCControlplaneManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(EDC_CONTROLPLANE.name())
				.triggerIdforinsert(triger.getTriggerId()).build();
		try {

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			inputData.put("edcapi-key", "X-Api-Key");
			inputData.put("edcapi-key-value", generateRandomPassword);
			inputData.put("dataplanepublicurl", dnsNameURLProtocol + "://" + customerDetails.getTenantName()
					+ "edcdataplane-edc-dataplane:8185/api/public");

			String controlplaneurl = dnsNameURLProtocol + "://" + dnsName;

			String edcDb = "jdbc:postgresql://" + customerDetails.getTenantName()
					+ "edcpostgresdb-postgresql:5432/postgres";
			inputData.put("edcdatabaseurl", edcDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(EDC_CONTROLPLANE, customerDetails.getTenantName(), inputData);
			else
				appManagement.updatePackage(EDC_CONTROLPLANE, customerDetails.getTenantName(), inputData);

			outputData.put("controlplanevalidationendpoint", dnsNameURLProtocol + "://"
					+ customerDetails.getTenantName() + "edccontrolplane-edc-controlplane:8182/validation/token");

			outputData.put("controlplaneendpoint", controlplaneurl);
			outputData.put("controlplanedataendpoint", controlplaneurl + "/data");
			outputData.put("edcapi-key", "X-Api-Key");
			outputData.put("edcapi-key-value", generateRandomPassword);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("EDCControlplaneMaanger failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("EDCControlplaneMaanger Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return outputData;
	}

}
