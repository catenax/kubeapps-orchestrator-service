package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.POSTGRES_DB;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostgresDBManager {

	private final KubeAppsPackageManagement appManagement;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData) {

		String packagefor = inputData.get("packagefor");

		inputData.put("postgresPassword", "admin@123");
		inputData.put("username", "admin");
		inputData.put("password", "admin@123");
		inputData.put("database", customerDetails.getTenantName() + "_" + packagefor);

		if (AppActions.ADD.equals(action))
			appManagement.createPackage(POSTGRES_DB, customerDetails.getTenantName() + "" + packagefor,
					inputData);
		else
			appManagement.updatePackage(POSTGRES_DB, customerDetails.getTenantName() + "" + packagefor,
					inputData);

		return inputData;
	}

}
