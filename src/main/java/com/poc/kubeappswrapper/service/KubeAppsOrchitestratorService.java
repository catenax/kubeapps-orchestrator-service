package com.poc.kubeappswrapper.service;

import static com.poc.kubeappswrapper.constant.AppConstant.EDC_CONTROLPLANE;
import static com.poc.kubeappswrapper.constant.AppConstant.EDC_DATAPLANE;
import static com.poc.kubeappswrapper.constant.AppConstant.POSTGRES_DB;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.proxy.kubeapps.KubeAppManageProxy;
import com.poc.kubeappswrapper.utility.CertificateManager;
import com.poc.kubeappswrapper.utility.DAPsManager;
import com.poc.kubeappswrapper.utility.VaultManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubeAppsOrchitestratorService {

	private final KubeAppManageProxy kubeAppManageProxy;
	private final KubeAppsPackageManagement appManagement;

	@Autowired
	private CertificateManager certificateManager;

	@Autowired
	private DAPsManager dapsManager;

	@Autowired
	private VaultManager vaultManager;

	public String getAllInstallPackages() {
		return kubeAppManageProxy.getAllInstallPackages();
	}

	public String createPackage(String tenantName, String bpnNumber, String role) {

		Map<String, String> inputConfiguration = new HashMap<>();
		String clientId = certificateManager.createCertificate(tenantName);
		inputConfiguration.put("dapsclientid", clientId);


		Map<String, String> dapsConfiguration = dapsManager.registerClientInDAPs(clientId, tenantName, bpnNumber, role);
		inputConfiguration.putAll(dapsConfiguration);
		
		
		Map<String, String> tenantKeyinVault = vaultManager.uploadKeyandValues(clientId, tenantName);
		inputConfiguration.putAll(tenantKeyinVault);
		
		inputConfiguration.put("postgresPassword", "admin@123");
		inputConfiguration.put("username", "admin");
		inputConfiguration.put("password", "admin@123");
		inputConfiguration.put("database", "edc_provider");

		appManagement.createPackage(POSTGRES_DB, tenantName, inputConfiguration);

		appManagement.createPackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);

		appManagement.createPackage(EDC_DATAPLANE, tenantName, inputConfiguration);
		

		return "AppInstall";
	}

	public String updatePackage(String tenantName, String bpnNumber, String role) {

		appManagement.updatePackage(POSTGRES_DB, tenantName, null);
		appManagement.updatePackage(EDC_CONTROLPLANE, tenantName, null);
		appManagement.updatePackage(EDC_DATAPLANE, tenantName, null);

		return "AppUpdate";
	}

	public String deletePackage(String tenantName) {
		Map<String, String> inputConfiguration = new HashMap<>();
		appManagement.deletePackage(POSTGRES_DB, tenantName, inputConfiguration);
		appManagement.deletePackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(EDC_DATAPLANE, tenantName, inputConfiguration);

		return "Appdeleted";

	}

}
