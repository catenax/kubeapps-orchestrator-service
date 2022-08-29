package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;

import java.util.Map;
import java.util.UUID;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
public class DFTBackendManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final PortalIntegrationManager portalIntegrationManager;
	private int counter;

	@Retryable(value = { ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString())
				.step(DFT_BACKEND.name())
				.triggerIdforinsert(triger.getTriggerId())
				.build();
		try {
			String dnsName = inputData.get("dnsName");

			inputData.put("manufacturerId", customerDetails.getBpnNumber());

			inputData.put("dftfrontendurl",
					"http://" + dnsName + "/" + customerDetails.getTenantName() + "dftfrontend");
			Map<String, String> portalDetails = portalIntegrationManager.getDigitalandKeyCloackDetails(customerDetails,
					inputData);
			inputData.putAll(portalDetails);

			String dftDb = "jdbc:postgresql://" + customerDetails.getTenantName()
					+ "dftpostgresdb-postgresql:5432/postgres";
			inputData.put("dftdatabaseurl", dftDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(DFT_BACKEND, customerDetails.getTenantName(), inputData);
			else
				appManagement.updatePackage(DFT_BACKEND, customerDetails.getTenantName(), inputData);

			String backendurl = "http://" + dnsName + "/" + customerDetails.getTenantName() + "dftbackend";

			inputData.put("dftbackendurl", backendurl);
			inputData.put("dftbackendapikey", PasswordGenerator.generateRandomPassword(60));

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {
			
			counter++;
			log.info("DftBackendManager failed retry attempt: "+counter);
			
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DftBackendManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}
		return inputData;
	}
}