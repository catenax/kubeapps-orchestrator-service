package com.poc.kubeappswrapper.service;

import static com.poc.kubeappswrapper.constant.AppActions.CREATE;
import static com.poc.kubeappswrapper.constant.AppActions.DELETE;
import static com.poc.kubeappswrapper.constant.AppActions.UPDATE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_FRONTEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.POSTGRES_DB;


import com.poc.kubeappswrapper.manager.EmailManager;
import com.poc.kubeappswrapper.model.EmailRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.manager.AutoSetupTriggerManager;
import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.model.DFTUpdateRequest;
import com.poc.kubeappswrapper.proxy.kubeapps.KubeAppManageProxy;
import com.poc.kubeappswrapper.repository.AutoSetupTriggerEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KubeAppsOrchitestratorService {

	private final KubeAppManageProxy kubeAppManageProxy;
	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final EDCConnectorWorkFlow edcConnectorWorkFlow;
	private final DFTAppWorkFlow dftWorkFlow;

	@Autowired
	private AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;

	@Autowired
	private EmailManager emailManager;

	@Value("${target.cluster}")
	private String targetCluster;

	@Value("${target.namespace}")
	private String targetNamespace;

	@Value("${dns.name}")
	private String dnsName;
	
	@Value("${dns.name}")
	private String dnsNameURLProtocol;
	
	

	public String getAllInstallPackages() {
		return kubeAppManageProxy.getAllInstallPackages();
	}

	public String createPackage(CustomerDetails customerDetails) {

		String targetNamespace = getTenantName(customerDetails);
		
		dnsName = dnsName.replace("tenantname", targetNamespace); 
		
		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();
		inputConfiguration.put("dnsName", dnsName);
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		String triggerId = UUID.randomUUID().toString();

		Runnable runnable = () -> {
			String namespaces = kubeAppManageProxy.getNamespaces(targetCluster, targetNamespace);
			if (!namespaces.contains("true"))
				kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);

			proceessTrigger(customerDetails, targetNamespace, CREATE, triggerId, inputConfiguration);
		};

		new Thread(runnable).start();

		return triggerId;
	}

	public String updatePackage(CustomerDetails customerDetails) {

		String targetNamespace = getTenantName(customerDetails);;

		dnsName = dnsName.replace("tenantname", targetNamespace); 
		
		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();
		inputConfiguration.put("dnsName", dnsName);
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);
		
		String triggerId = UUID.randomUUID().toString();

		Runnable runnable = () -> proceessTrigger(customerDetails, targetNamespace, UPDATE, triggerId,
				inputConfiguration);

		new Thread(runnable).start();

		return triggerId;
	}

	private void proceessTrigger(CustomerDetails customerDetails, String targetNamespace, AppActions action,
			String triggerId, Map<String, String> inputConfiguration) {
		
		AutoSetupTriggerEntry createTrigger = null;
		
		try {
			createTrigger = autoSetupTriggerManager.createTrigger(customerDetails, action, triggerId);

			Map<String, String> edcOutput = edcConnectorWorkFlow.getWorkFlow(customerDetails, action,
					inputConfiguration, createTrigger);

			inputConfiguration.putAll(edcOutput);

			Map<String, String> map = dftWorkFlow.getWorkFlow(customerDetails, action, inputConfiguration,
					createTrigger);

			//Send an email
			Map<String, Object> emailContent = new HashMap<>();
			emailContent.put("name", customerDetails.getOrganizationName());
			emailContent.put("dftfrontendurl", map.get("dftfrontendurl"));
			emailContent.put("dftbackendurl", map.get("dftbackendurl"));
			EmailRequest emailRequest = EmailRequest.builder()
					.emailContent(emailContent)
					.subject("DFT Application Deployed Successfully")
					.templateFileName("success.html")
					.build();
			emailManager.sendEmail(emailRequest);
			log.info("Email sent successfully");
			//End of email sending code

			Map<String, String> resultMap = new ConcurrentHashMap<>();
			resultMap.put("dftfrontendurl", map.get("dftfrontendurl"));
			resultMap.put("dftbackendurl", map.get("dftbackendurl"));
			resultMap.put("controlplanedataendpoint", map.get("controlplanedataendpoint"));
			resultMap.put("dataplanepublicendpoint", map.get("dataplanepublicendpoint"));
			resultMap.put("edcapi-key", inputConfiguration.get("edcapi-key"));
			resultMap.put("edcapi-key-value", inputConfiguration.get("edcapi-key-value"));

			// log.info(resultMap.toString());
			String json = new ObjectMapper().writeValueAsString(resultMap);
			createTrigger.setAutosetupResult(json);
			createTrigger.setStatus(TriggerStatusEnum.MANUAL_UPDATE_PENDING.name());
			log.info("All Packages created/updated successfully!!!!");
		} catch (Exception e) {
			log.error("Error in package creation " + e.getMessage());
			createTrigger.setStatus(TriggerStatusEnum.FAILED.name());
			createTrigger.setRemark(e.getMessage());
		}
		LocalDateTime now = LocalDateTime.now();
		createTrigger.setModifiedTimestamp(now.toString());
		autoSetupTriggerManager.saveTriggerUpdate(createTrigger);
	}

	public String deletePackage(CustomerDetails customerDetails) {

		getTenantName(customerDetails);

		String targetNamespace = customerDetails.getTenantName();

		dnsName = dnsName.replace("tenantname", targetNamespace); 
		
		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();
		inputConfiguration.put("dnsName", dnsName);
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		String triggerId = UUID.randomUUID().toString();
		Runnable runnable = () -> processDeleteTrigger(customerDetails, targetNamespace, triggerId, inputConfiguration);

		new Thread(runnable).start();

		return triggerId;

	}

	private String getTenantName(CustomerDetails customerDetails) {
		
		customerDetails.setTenantName(Optional.ofNullable(customerDetails.getOrganizationName()).map(orgname -> {
			orgname = orgname.replaceAll("[^a-zA-Z0-9]", "");
			return orgname.length() < 6 ? orgname : orgname.substring(0, 6);
		}).orElseThrow(() -> new RuntimeException("Organization name should not be null")));
		
		return customerDetails.getTenantName();
	}

	private void processDeleteTrigger(CustomerDetails customerDetails, String targetNamespace, String triggerId,
			Map<String, String> inputConfiguration) {

		String tenantName = customerDetails.getTenantName();
		AutoSetupTriggerEntry createTrigger = autoSetupTriggerManager.createTrigger(customerDetails, DELETE, triggerId);

		appManagement.deletePackage(POSTGRES_DB, tenantName + "edc", inputConfiguration);
		appManagement.deletePackage(EDC_CONTROLPLANE, tenantName, inputConfiguration);
		appManagement.deletePackage(EDC_DATAPLANE, tenantName, inputConfiguration);

		appManagement.deletePackage(POSTGRES_DB, tenantName + "dft", inputConfiguration);
		appManagement.deletePackage(DFT_BACKEND, tenantName, inputConfiguration);
		appManagement.deletePackage(DFT_FRONTEND, tenantName, inputConfiguration);
		createTrigger.setStatus(TriggerStatusEnum.SUCCESS.name());
		autoSetupTriggerManager.saveTriggerUpdate(createTrigger);
		log.info("All Packages deleted successfully!!!!");

	}

	public String updateDftPackage(DFTUpdateRequest dftUpdateRequest) {
		try {
			AutoSetupTriggerEntry autoSetupTriggerEntry = autoSetupTriggerEntryRepository.findTop1ByBpnNumberAndTriggerTypeAndStatusOrderByCreatedTimestampDesc(dftUpdateRequest.getBpnNumber(), "CREATE", "MANUAL_UPDATE_PENDING");
			if (autoSetupTriggerEntry != null) {
				ObjectMapper mapper = new ObjectMapper();
				CustomerDetails customerDetails = mapper.readValue(autoSetupTriggerEntry.getAutosetupRequest(), CustomerDetails.class);
				String targetNamespace = getTenantName(customerDetails);;

				Map<String, String> inputConfiguration = new ConcurrentHashMap<>();
				inputConfiguration.put("dnsName", dnsName);
				inputConfiguration.put("targetCluster", targetCluster);
				inputConfiguration.put("targetNamespace", targetNamespace);
				inputConfiguration.put("dthostname", dftUpdateRequest.getDigitalTwinUrl());
				inputConfiguration.put("dtauthurl", dftUpdateRequest.getDigitalTwinAuthUrl());
				inputConfiguration.put("dtclientId", dftUpdateRequest.getDigitalTwinClientId());
				inputConfiguration.put("dtclientsecret", dftUpdateRequest.getDigitalTwinClientSecret());
				inputConfiguration.put("kcrealm", dftUpdateRequest.getKeyclackRealm());
				inputConfiguration.put("kcurl", dftUpdateRequest.getKeycloackUrl());
				inputConfiguration.put("kcresource", dftUpdateRequest.getKeycloackClientId());
				
				Map<String, String> autosetupResult = new ObjectMapper().readValue(autoSetupTriggerEntry.getAutosetupResult(),
						HashMap.class);
				inputConfiguration.putAll(autosetupResult);
				String triggerId = UUID.randomUUID().toString();

				AutoSetupTriggerEntry createTrigger = autoSetupTriggerManager.createTrigger(customerDetails, UPDATE, triggerId);

				Runnable runnable = () -> {
					try {
						Map<String, String> map = dftWorkFlow.getWorkFlow(customerDetails, UPDATE, inputConfiguration, createTrigger);
						// log.info(resultMap.toString());
						Map<String, String> resultMap = new ConcurrentHashMap<>();
						resultMap.put("dftfrontendurl", map.get("dftfrontendurl"));
						resultMap.put("dftbackendurl", map.get("dftbackendurl"));
						resultMap.put("controlplanedataendpoint", map.get("controlplanedataendpoint"));
						resultMap.put("dataplanepublicendpoint", map.get("dataplanepublicendpoint"));
						resultMap.put("edcapi-key", inputConfiguration.get("edcapi-key"));
						resultMap.put("edcapi-key-value", inputConfiguration.get("edcapi-key-value"));

						String json = new ObjectMapper().writeValueAsString(resultMap);
						createTrigger.setAutosetupResult(json);
						createTrigger.setStatus(TriggerStatusEnum.SUCCESS.name());
						log.info("DFT Packages updated successfully!!!!");
					} catch (Exception e) {
						log.error("Error in package creation " + e.getMessage());
						createTrigger.setStatus(TriggerStatusEnum.FAILED.name());
						createTrigger.setRemark(e.getMessage());
					}
					LocalDateTime now = LocalDateTime.now();
					createTrigger.setModifiedTimestamp(now.toString());
					autoSetupTriggerManager.saveTriggerUpdate(createTrigger);
				};
				new Thread(runnable).start();

				return triggerId;
			}
			return "Autosetup create entry not present";
		} catch (Exception e) {
			return "Exception in updating dft package : " + e.getMessage();
		}

	}
}
