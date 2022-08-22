package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EDCControlplaneManager {

	private final KubeAppsPackageManagement appManagement;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData) {

		String dsnName = inputData.get("dsnName");
		String databasefor = inputData.get("database");
		String packagefor = inputData.get("packagefor");

		inputData.put("edcapi-key", "X-Api-Key");
		inputData.put("edcapi-key-value", "password");
		inputData.put("dataplanepublicurl",
				"http://" + customerDetails.getTenantName() + "edcdataplane-edc-dataplane:8185/api/public");

		String edcDb = "jdbc:postgresql://" + customerDetails.getTenantName() + packagefor
				+ "postgresdb-postgresql:5432/" + databasefor;
		inputData.put("edcdatabaseurl", edcDb);

		if (AppActions.ADD.equals(action))
			appManagement.createPackage(EDC_CONTROLPLANE, customerDetails.getTenantName(), inputData);
		else
			appManagement.updatePackage(EDC_CONTROLPLANE, customerDetails.getTenantName(), inputData);

		Map<String, String> outputData = new HashMap<>();
		outputData.put("controlplanevalidationendpoint",
				customerDetails.getTenantName() + "edccontrolplane-edc-controlplane");
		outputData.put("controlplanedataendpoint", "http://" + dsnName + ":8181/data");
		outputData.put("edcapi-key", "X-Api-Key");
		outputData.put("edcapi-key-value", "password");
		return outputData;
	}

}
