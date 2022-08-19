package com.poc.kubeappswrapper.service;

import static com.poc.kubeappswrapper.constant.AppConstant.DFT_BACKEND;
import static com.poc.kubeappswrapper.constant.AppConstant.DFT_FRONTEND;
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
import com.poc.kubeappswrapper.utility.KeyClockManager;
import com.poc.kubeappswrapper.utility.VaultManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KubeAppsOrchitestratorService {

	private final KubeAppManageProxy kubeAppManageProxy;
	private final KubeAppsPackageManagement appManagement;

	@Autowired
	private CertificateManager certificateManager;

	@Autowired
	private DAPsManager dapsManager;

	@Autowired
	private VaultManager vaultManager;
	
	@Autowired
	private KeyClockManager keyClockServiceManager;
	

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
		
		String edcDb = "jdbc:postgresql://" + tenantName + "postgresdb-postgresql:5432/edc_provider";
		inputConfiguration.put("edcdatabaseurl", edcDb);
		appManagement.createPackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);

		appManagement.createPackage(EDC_DATAPLANE, tenantName, inputConfiguration);
		
		
		inputConfiguration.put("dftdatabase", "edc_provider");
		
		String dftDb = "jdbc:postgresql://" + tenantName + "postgresdb-postgresql:5432/edc_provider";
		inputConfiguration.put("dftdatabaseurl", dftDb);
		
//		Map<String, String> keclock = vaultManager.uploadKeyandValues(clientId, tenantName);
//		inputConfiguration.putAll(keclock);
//		
//		Map<String, String> digitalDetails = vaultManager.uploadKeyandValues(clientId, tenantName);
//		inputConfiguration.putAll(digitalDetails);
//		
//		Map<String, String> edcDetails = vaultManager.uploadKeyandValues(clientId, tenantName);
//		inputConfiguration.putAll(edcDetails);
		
		appManagement.createPackage(DFT_BACKEND, tenantName, inputConfiguration);

		inputConfiguration.put("dftbackendurl","");
		inputConfiguration.put("dftbackendapikey","");
		inputConfiguration.put("dftkeyclockurl","");
		inputConfiguration.put("dftkeyclockrealm","");
		inputConfiguration.put("dftkeyclockclientid","");
		
		appManagement.createPackage(DFT_FRONTEND, tenantName, inputConfiguration);
		

		return "AppInstall";
	}

	public String updatePackage(String tenantName, String bpnNumber, String role) {

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

		
		appManagement.updatePackage(POSTGRES_DB, tenantName, inputConfiguration);
		
		appManagement.updatePackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);
		
		appManagement.updatePackage(EDC_DATAPLANE, tenantName, inputConfiguration);

		return "AppUpdate";
	}

	public String deletePackage(String tenantName) {
		Map<String, String> inputConfiguration = new HashMap<>();
		appManagement.deletePackage(POSTGRES_DB, tenantName, inputConfiguration);
		appManagement.deletePackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(EDC_DATAPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(DFT_BACKEND, tenantName, inputConfiguration);
		appManagement.deletePackage(DFT_FRONTEND, tenantName, inputConfiguration);
		return "Appdeleted";

	}

}
