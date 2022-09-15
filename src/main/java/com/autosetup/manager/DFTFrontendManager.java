package com.autosetup.manager;

import static com.autosetup.constant.AppNameConstant.DFT_FRONTEND;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class DFTFrontendManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(DFT_FRONTEND.name()).triggerIdforinsert(triger.getTriggerId())
				.build();
		try {
			
			String packageName = tool.getPackageName();
			
			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(DFT_FRONTEND, packageName, inputData);
			else
				appManagement.updatePackage(DFT_FRONTEND, packageName, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("DftFrontendManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DftFrontendManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return inputData;
	}
}