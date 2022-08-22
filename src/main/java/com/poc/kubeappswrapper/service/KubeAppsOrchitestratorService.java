package com.poc.kubeappswrapper.service;

import static com.poc.kubeappswrapper.constant.AppActions.ADD;
import static com.poc.kubeappswrapper.constant.AppActions.UPDATE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_FRONTEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.POSTGRES_DB;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.manager.CertificateManager;
import com.poc.kubeappswrapper.manager.DAPsManager;
import com.poc.kubeappswrapper.manager.DFTBackendManager;
import com.poc.kubeappswrapper.manager.DFTFrontendManager;
import com.poc.kubeappswrapper.manager.EDCControlplaneManager;
import com.poc.kubeappswrapper.manager.EDCDataplaneManager;
import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.manager.PostgresDBManager;
import com.poc.kubeappswrapper.manager.VaultManager;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.proxy.kubeapps.KubeAppManageProxy;
import com.poc.kubeappswrapper.repository.AutoSetupTriggerEntryRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KubeAppsOrchitestratorService {

	private final KubeAppManageProxy kubeAppManageProxy;
	private final KubeAppsPackageManagement appManagement;

	private final CertificateManager certificateManager;
	private final DAPsManager dapsManager;
	private final VaultManager vaultManager;
	private final PostgresDBManager postgresManager;
	private final EDCControlplaneManager edcControlplaneManager;
	private final EDCDataplaneManager edcDataplaneManager;
	private final DFTBackendManager dftBackendManager;
	private final DFTFrontendManager dftFrontendManager;

	private AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;

	public String getAllInstallPackages() {
		return kubeAppManageProxy.getAllInstallPackages();
	}

	public String createPackage(CustomerDetails customerDetails) {

		customerDetails.setTenantName(Optional.ofNullable(customerDetails.getOrganizationName()).map(orgname -> {
			orgname = orgname.replaceAll("[^a-zA-Z0-9]", "");
			return orgname.length() < 6 ? orgname : orgname.substring(0, 6);
		}).orElseThrow(() -> new RuntimeException("Organization name should not be null")));

		AutoSetupTriggerEntry autoSetupTriggerEntry=AutoSetupTriggerEntry.builder()
				.autosetupTenantName(customerDetails.getTenantName()).build();
		//autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
		
		String targetCluster = "default";
		//String targetNamespace = customerDetails.getTenantName();
		String targetNamespace = "kubeapps";
		
		Map<String, String> inputConfiguration = new HashMap<>();
		inputConfiguration.put("dsnName", "localhost");
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		//kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);

		Map<String, String> certificateConfiguration = certificateManager.createCertificate(customerDetails,
				inputConfiguration);
		inputConfiguration.putAll(certificateConfiguration);

		Map<String, String> dapsConfiguration = dapsManager.registerClientInDAPs(customerDetails,
				certificateConfiguration);
		inputConfiguration.putAll(dapsConfiguration);

		Map<String, String> tenantKeyinVault = vaultManager.uploadKeyandValues(customerDetails, inputConfiguration);
		inputConfiguration.putAll(tenantKeyinVault);

		inputConfiguration.put("packagefor", "edc");
		Map<String, String> edcpostgresConfiguration = postgresManager.managePackage(customerDetails, ADD,
				inputConfiguration);
		inputConfiguration.putAll(edcpostgresConfiguration);

		Map<String, String> edcControlplaneConfiguration = edcControlplaneManager.managePackage(customerDetails, ADD,
				inputConfiguration);
		inputConfiguration.putAll(edcControlplaneConfiguration);

		Map<String, String> edcDataplaneConfiguration = edcDataplaneManager.managePackage(customerDetails, ADD,
				inputConfiguration);
		inputConfiguration.putAll(edcDataplaneConfiguration);

		inputConfiguration.put("packagefor", "dft");
		Map<String, String> dftpostgresConfiguration = postgresManager.managePackage(customerDetails, ADD,
				inputConfiguration);
		inputConfiguration.putAll(dftpostgresConfiguration);

		Map<String, String> dftBackendConfiguration = dftBackendManager.managePackage(customerDetails, ADD,
				inputConfiguration);
		inputConfiguration.putAll(dftBackendConfiguration);

		Map<String, String> dftFrontendConfiguration = dftFrontendManager.managePackage(customerDetails, ADD,
				inputConfiguration);
		inputConfiguration.putAll(dftFrontendConfiguration);

		return "AppInstall";
	}

	public String updatePackage(CustomerDetails customerDetails) {

		customerDetails.setTenantName(Optional.ofNullable(customerDetails.getOrganizationName()).map(orgname -> {
			orgname = orgname.replaceAll("[^a-zA-Z0-9]", "");
			return orgname.length() < 6 ? orgname : orgname.substring(0, 6);
		}).orElseThrow(() -> new RuntimeException("Organization name should not be null")));

		String targetCluster = "default";
		// String targetNamespace = customerDetails.getTenantName();
		String targetNamespace = "kubeapps";

		Map<String, String> inputConfiguration = new HashMap<>();
		inputConfiguration.put("dsnName", "localhost");
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		Map<String, String> certificateConfiguration = certificateManager.createCertificate(customerDetails,
				inputConfiguration);
		inputConfiguration.putAll(certificateConfiguration);

		Map<String, String> dapsConfiguration = dapsManager.registerClientInDAPs(customerDetails,
				certificateConfiguration);
		inputConfiguration.putAll(dapsConfiguration);

		Map<String, String> tenantKeyinVault = vaultManager.uploadKeyandValues(customerDetails, inputConfiguration);
		inputConfiguration.putAll(tenantKeyinVault);

		inputConfiguration.put("packagefor", "edc");
		Map<String, String> edcpostgresConfiguration = postgresManager.managePackage(customerDetails, UPDATE,
				inputConfiguration);
		inputConfiguration.putAll(edcpostgresConfiguration);

		Map<String, String> edcControlplaneConfiguration = edcControlplaneManager.managePackage(customerDetails,
				AppActions.UPDATE, inputConfiguration);
		inputConfiguration.putAll(edcControlplaneConfiguration);

		Map<String, String> edcDataplaneConfiguration = edcDataplaneManager.managePackage(customerDetails,
				AppActions.UPDATE, inputConfiguration);
		inputConfiguration.putAll(edcDataplaneConfiguration);

		inputConfiguration.put("packagefor", "dft");
		Map<String, String> dftpostgresConfiguration = postgresManager.managePackage(customerDetails, AppActions.UPDATE,
				inputConfiguration);
		inputConfiguration.putAll(dftpostgresConfiguration);

		Map<String, String> dftBackendConfiguration = dftBackendManager.managePackage(customerDetails,
				AppActions.UPDATE, inputConfiguration);
		inputConfiguration.putAll(dftBackendConfiguration);

		Map<String, String> dftFrontendConfiguration = dftFrontendManager.managePackage(customerDetails,
				AppActions.UPDATE, inputConfiguration);
		inputConfiguration.putAll(dftFrontendConfiguration);

		return "AppUpdate";
	}

	public String deletePackage(CustomerDetails customerDetails) {

		customerDetails.setTenantName(Optional.ofNullable(customerDetails.getOrganizationName()).map(orgname -> {
			orgname = orgname.replaceAll("[^a-zA-Z0-9]", "");
			return orgname.length() < 6 ? orgname : orgname.substring(0, 6);
		}).orElseThrow(() -> new RuntimeException("Organization name should not be null")));

		String tenantName = customerDetails.getTenantName();
		String targetCluster = "default";
		// String targetNamespace = customerDetails.getTenantName();
		String targetNamespace = "kubeapps";

		Map<String, String> inputConfiguration = new HashMap<>();
		inputConfiguration.put("dsnName", "localhost");
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		appManagement.deletePackage(POSTGRES_DB, tenantName + "edc", inputConfiguration);
		appManagement.deletePackage(POSTGRES_DB, tenantName + "dft", inputConfiguration);
		appManagement.deletePackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(EDC_DATAPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(DFT_BACKEND, tenantName, inputConfiguration);
		appManagement.deletePackage(DFT_FRONTEND, tenantName, inputConfiguration);

		return "Appdeleted";

	}

}
