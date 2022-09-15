package com.autosetup.manager;

import static com.autosetup.constant.AppNameConstant.DFT_BACKEND;

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
public class DFTBackendManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final PortalIntegrationManager portalIntegrationManager;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(DFT_BACKEND.name()).triggerIdforinsert(triger.getTriggerId())
				.build();
		try {
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			inputData.put("manufacturerId", inputData.get("bpnNumber"));

			String backendurl = dnsNameURLProtocol + "://" + dnsName + "/dftbackend/api";
			String dftfrontend = dnsNameURLProtocol + "://" + dnsName;

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
			inputData.put("dftbackendurl", backendurl);
			inputData.put("dftbackendapikey", generateRandomPassword);
			inputData.put("dftbackendapiKeyHeader", "API_KEY");
			inputData.put("dftfrontendurl", dftfrontend);

			Map<String, String> portalDetails = portalIntegrationManager.getDigitalandKeyCloackDetails(customerDetails,
					inputData);
			inputData.putAll(portalDetails);

			String packageName = tool.getPackageName();
			
			String dftDb = "jdbc:postgresql://" + packageName + "-postgresdb-postgresql:5432/postgres";
			inputData.put("dftdatabaseurl", dftDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(DFT_BACKEND, packageName, inputData);
			else
				appManagement.updatePackage(DFT_BACKEND, packageName, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("DftBackendManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DftBackendManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}
		return inputData;
	}
}