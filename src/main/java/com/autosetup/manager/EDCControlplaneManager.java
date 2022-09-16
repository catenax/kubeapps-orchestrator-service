package com.autosetup.manager;

import static com.autosetup.constant.AppNameConstant.EDC_CONTROLPLANE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import com.autosetup.constant.AppActions;
import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.ServiceException;
import com.autosetup.model.Customer;
import com.autosetup.model.SelectedTools;
import com.autosetup.utility.PasswordGenerator;

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
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(EDC_CONTROLPLANE.name())
				.triggerIdforinsert(triger.getTriggerId()).build();
		try {
			String packageName = tool.getPackageName();

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			inputData.put("edcApiKey", "X-Api-Key");
			inputData.put("edcApiKeyValue", generateRandomPassword);
			inputData.put("dataPlanePublicUrl",
					dnsNameURLProtocol + "://" + tool.getLabel() + "-edcdataplane-edc-dataplane:8185/api/public");

			String controlplaneurl = dnsNameURLProtocol + "://" + dnsName;

			String edcDb = "jdbc:postgresql://" + packageName + "-postgresdb-postgresql:5432/postgres";
			inputData.put("edcdatabaseurl", edcDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(EDC_CONTROLPLANE, packageName, inputData);
			else
				appManagement.updatePackage(EDC_CONTROLPLANE, packageName, inputData);

			
			outputData.put("controlPlaneValidationEndpoint", dnsNameURLProtocol + "://" + packageName
					+ "edccontrolplane-edc-controlplane:8182/validation/token");

			
			outputData.put("controlPlaneEndpoint", controlplaneurl);
			outputData.put("controlPlaneDataEndpoint", controlplaneurl + "/data");
			outputData.put("edcApiKey", "X-Api-Key");
			outputData.put("edcApiKeyValue", generateRandomPassword);
			outputData.put("controlPlaneIdsEndpoint", controlplaneurl+"/api/v1/ids/data");
			

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