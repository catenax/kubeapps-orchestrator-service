package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.utility.PasswordGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EDCControlplaneManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString())
				.step(EDC_CONTROLPLANE.name())
				.triggerIdforinsert(triger.getTriggerId())
				.build();
		try {

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
			String dnsName = inputData.get("dnsName");

			inputData.put("edcapi-key", "X-Api-Key");
			inputData.put("edcapi-key-value", generateRandomPassword);
			inputData.put("dataplanepublicurl",
					"http://" + customerDetails.getTenantName() + "edcdataplane-edc-dataplane:8185/api/public");

			String edcDb = "jdbc:postgresql://" + customerDetails.getTenantName()
					+ "edcpostgresdb-postgresql:5432/postgres";
			inputData.put("edcdatabaseurl", edcDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(EDC_CONTROLPLANE, customerDetails.getTenantName(), inputData);
			else
				appManagement.updatePackage(EDC_CONTROLPLANE, customerDetails.getTenantName(), inputData);

			String controlplaneurl = "http://" + dnsName + "/" + customerDetails.getTenantName()
					+ "edccontrolplane/data";

			outputData.put("controlplanevalidationendpoint",
					customerDetails.getTenantName() + "edccontrolplane-edc-controlplane");
			outputData.put("controlplanedataendpoint", controlplaneurl);
			outputData.put("edcapi-key", "X-Api-Key");
			outputData.put("edcapi-key-value", generateRandomPassword);


			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("EDCControlplaneMaanger Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return outputData;
	}

}
