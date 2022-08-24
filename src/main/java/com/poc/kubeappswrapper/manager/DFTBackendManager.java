package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DFTBackendManager {

	private final KubeAppsPackageManagement appManagement;

	private final PortalIntegrationManager portalIntegrationManager;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action, 
			Map<String, String> inputData) {

		String dsnName = inputData.get("dsnName");

		inputData.put("manufacturerId", customerDetails.getBpnNumber());

		inputData.put("dftfrontendurl", "http://" + dsnName + ":8080/");
		Map<String, String> portalDetails = portalIntegrationManager.getDigitalandKeyCloackDetails(customerDetails,
				inputData);
		inputData.putAll(portalDetails);

		String dftDb = "jdbc:postgresql://" + customerDetails.getTenantName()  
				+ "dftpostgresdb-postgresql:5432/postgres";
		inputData.put("dftdatabaseurl", dftDb);

		if (AppActions.ADD.equals(action))
			appManagement.createPackage(DFT_BACKEND, customerDetails.getTenantName(), inputData);
		else
			appManagement.updatePackage(DFT_BACKEND, customerDetails.getTenantName(), inputData);

		inputData.put("dftbackendurl", "http://" + dsnName + "dftbackend:8080");
		inputData.put("dftbackendapikey", "ec8de3db3504b3b38a09536236ebbac2bd55f253bfcff43e2e6cf43248e110fc");

		return inputData;
	}
}