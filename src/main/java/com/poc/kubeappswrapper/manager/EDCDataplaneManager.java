package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EDCDataplaneManager {

	private final KubeAppsPackageManagement appManagement;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData) {

		String dsnName = inputData.get("dsnName");

		if (AppActions.ADD.equals(action))
			appManagement.createPackage(EDC_DATAPLANE, customerDetails.getTenantName(), inputData);
		else
			appManagement.updatePackage(EDC_DATAPLANE, customerDetails.getTenantName(), inputData);

		Map<String, String> outputData = new HashMap<>();
		outputData.put("dataplanepublicendpoint", "http://" + dsnName + ":8185/api/public");
		return inputData;
	}
}
