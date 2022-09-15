package com.autosetup.manager;

import static com.autosetup.constant.AppNameConstant.EDC_DATAPLANE;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EDCDataplaneManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(EDC_DATAPLANE.name()).triggerIdforinsert(triger.getTriggerId())
				.build();
		try {
			String packageName = tool.getPackageName();
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			String dataplaneurl = dnsNameURLProtocol + "://" + dnsName;

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(EDC_DATAPLANE, packageName, inputData);
			else
				appManagement.updatePackage(EDC_DATAPLANE, packageName, inputData);

			inputData.put("dataplaneendpoint", dataplaneurl);
			inputData.put("dataPlanePublicEndpoint", dataplaneurl + "/public");

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("EDCDataplaneManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());

			throw new ServiceException("EDCDataplaneManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return inputData;
	}
}
