package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_FRONTEND;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DFTFrontendManager {

	private final KubeAppsPackageManagement appManagement;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData) {

		String dsnName = inputData.get("dsnName");
		
		if (AppActions.ADD.equals(action))
			appManagement.createPackage(DFT_FRONTEND, customerDetails.getTenantName(), inputData);
		else
			appManagement.updatePackage(DFT_FRONTEND, customerDetails.getTenantName(), inputData);

		Map<String, String> outputData = new HashMap<>();
		outputData.put("dftfrontendurl", "http://" + dsnName + ":8080/");

		return outputData;
	}
}