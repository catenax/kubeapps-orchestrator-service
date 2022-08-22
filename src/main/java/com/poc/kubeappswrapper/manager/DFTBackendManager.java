package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DFTBackendManager {

	private final KubeAppsPackageManagement appManagement;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData) {

		String databasefor = inputData.get("database");
		String packagefor = inputData.get("packagefor");

		inputData.put("manufacturerId", customerDetails.getBpnNumber());

		String dftDb = "jdbc:postgresql://" + customerDetails.getTenantName() + packagefor
				+ "postgresdb-postgresql:5432/" + databasefor;
		inputData.put("dftdatabaseurl", dftDb);

		if (AppActions.ADD.equals(action))
			appManagement.createPackage(DFT_BACKEND, customerDetails.getTenantName(), inputData);
		else
			appManagement.updatePackage(DFT_BACKEND, customerDetails.getTenantName(), inputData);

		Map<String, String> outputData = new HashMap<>();
		outputData.put("dftbackendurl", "http://" + customerDetails.getTenantName() + "dftbackend:8080");
		outputData.put("dftbackendapikey", "ec8de3db3504b3b38a09536236ebbac2bd55f253bfcff43e2e6cf43248e110fc");

		return outputData;
	}
}