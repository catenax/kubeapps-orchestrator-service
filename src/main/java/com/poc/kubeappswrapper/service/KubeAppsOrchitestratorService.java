package com.poc.kubeappswrapper.service;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_FRONTEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.POSTGRES_DB;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.proxy.kubeapps.KubeAppManageProxy;
import com.poc.kubeappswrapper.repository.AutoSetupTriggerEntryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KubeAppsOrchitestratorService {

	private final KubeAppManageProxy kubeAppManageProxy;
	private final KubeAppsPackageManagement appManagement;

	private final EDCConnectorWorkFlow edcConnectorWorkFlow;
	private final DFTAppWorkFlow dftWorkFlow;

	private AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;
	
	
	@Value("${targetCluster}")
	private String targetCluster;

	@Value("${targetNamespace}")
	private String targetNamespace;

	@Value("${dnsName}")
	private String dnsName;

	public String getAllInstallPackages() {
		return kubeAppManageProxy.getAllInstallPackages();
	}

	public String createPackage(CustomerDetails customerDetails) {

		customerDetails.setTenantName(Optional.ofNullable(customerDetails.getOrganizationName()).map(orgname -> {
			orgname = orgname.replaceAll("[^a-zA-Z0-9]", "");
			return orgname.length() < 6 ? orgname : orgname.substring(0, 6);
		}).orElseThrow(() -> new RuntimeException("Organization name should not be null")));

		AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
				.autosetupTenantName(customerDetails.getTenantName()).build();
		// autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);

		// String targetNamespace = customerDetails.getTenantName();

		Map<String, String> inputConfiguration = new HashMap<>();
		inputConfiguration.put("dnsName", dnsName);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		// kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);

		CompletableFuture<Map<String, String>> edcControlplaneTask = edcConnectorWorkFlow.getWorkFlow(customerDetails,
				AppActions.ADD, inputConfiguration);
		CompletableFuture<Map<String, String>> workFlow = dftWorkFlow.getWorkFlow(edcControlplaneTask, customerDetails,
				AppActions.ADD, inputConfiguration);
		Map<String, String> map = null;
		try {
			map = workFlow.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return map.get("dftfrontendurl");
	}

	public String updatePackage(CustomerDetails customerDetails) {

		customerDetails.setTenantName(Optional.ofNullable(customerDetails.getOrganizationName()).map(orgname -> {
			orgname = orgname.replaceAll("[^a-zA-Z0-9]", "");
			return orgname.length() < 6 ? orgname : orgname.substring(0, 6);
		}).orElseThrow(() -> new RuntimeException("Organization name should not be null")));

		AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
				.autosetupTenantName(customerDetails.getTenantName()).build();
		// autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);

		String targetCluster = "default";
		// String targetNamespace = customerDetails.getTenantName();
		String targetNamespace = "kubeapps";

		Map<String, String> inputConfiguration = new HashMap<>();
		inputConfiguration.put("dsnName", "localhost");
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		// kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);

		CompletableFuture<Map<String, String>> edcControlplaneTask = edcConnectorWorkFlow.getWorkFlow(customerDetails,
				AppActions.UPDATE, inputConfiguration);
		CompletableFuture<Map<String, String>> workFlow = dftWorkFlow.getWorkFlow(edcControlplaneTask, customerDetails,
				AppActions.UPDATE, inputConfiguration);

		try {
			Map<String, String> map = workFlow.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		Map<String, String> map = null;
		try {
			map = workFlow.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return map.get("dftfrontendurl");
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
		appManagement.deletePackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(EDC_DATAPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(POSTGRES_DB, tenantName + "dft", inputConfiguration);
		appManagement.deletePackage(DFT_BACKEND, tenantName, inputConfiguration);
		appManagement.deletePackage(DFT_FRONTEND, tenantName, inputConfiguration);

		return "Appdeleted";

	}

}
