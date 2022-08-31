package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;

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
	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(EDC_DATAPLANE.name()).triggerIdforinsert(triger.getTriggerId())
				.build();
		try {

			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			String dataplaneurl = dnsNameURLProtocol + "://" + dnsName + "/edcdataplane";

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(EDC_DATAPLANE, customerDetails.getTenantName(), inputData);
			else
				appManagement.updatePackage(EDC_DATAPLANE, customerDetails.getTenantName(), inputData);

			inputData.put("dataplanepublicendpoint", dataplaneurl);

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
