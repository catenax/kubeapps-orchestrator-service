package com.poc.kubeappswrapper.service;

import static com.poc.kubeappswrapper.constant.AppActions.CREATE;
import static com.poc.kubeappswrapper.constant.AppActions.DELETE;
import static com.poc.kubeappswrapper.constant.AppActions.UPDATE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_FRONTEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;
import static com.poc.kubeappswrapper.constant.AppNameConstant.POSTGRES_DB;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ValidationException;
import com.poc.kubeappswrapper.manager.AutoSetupTriggerManager;
import com.poc.kubeappswrapper.manager.EmailManager;
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
	private String dnsOriginalName;

	@Value("${dns.name.protocol}")
	private String dnsNameURLProtocol;
	
	@Value("${portal.email.address}")
	private String portalEmail;
	
	public String getAllInstallPackages() {
		return kubeAppManageProxy.getAllInstallPackages();
	}

	public String createPackage(CustomerDetails customerDetails) {

		Map<String, String> inputConfiguration = prepareInputConfiguration(customerDetails);

		String triggerId = UUID.randomUUID().toString();

		Runnable runnable = () -> {

			String targetNamespace = inputConfiguration.get("targetNamespace");

			String namespaces = kubeAppManageProxy.getNamespaces(targetCluster, targetNamespace);
			if (!namespaces.contains("true"))
				kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);

			proceessTrigger(customerDetails, CREATE, triggerId, inputConfiguration);
		};

		new Thread(runnable).start();

		return triggerId;
	}

	public String updatePackage(CustomerDetails customerDetails) {

		Map<String, String> inputConfiguration = prepareInputConfiguration(customerDetails);

		String triggerId = UUID.randomUUID().toString();

		Runnable runnable = () -> proceessTrigger(customerDetails, UPDATE, triggerId, inputConfiguration);

		new Thread(runnable).start();

		return triggerId;
	}

	public String deletePackage(CustomerDetails customerDetails) {

		Map<String, String> inputConfiguration = prepareInputConfiguration(customerDetails);

		String triggerId = UUID.randomUUID().toString();
		Runnable runnable = () -> processDeleteTrigger(customerDetails, triggerId, inputConfiguration);

		new Thread(runnable).start();

		return triggerId;
	}


	private void proceessTrigger(CustomerDetails customerDetails, AppActions action, String triggerId,
			Map<String, String> inputConfiguration) {

		AutoSetupTriggerEntry createTrigger = null;

		try {
			createTrigger = autoSetupTriggerManager.createTrigger(customerDetails, action, triggerId);

			Map<String, String> edcOutput = edcConnectorWorkFlow.getWorkFlow(customerDetails, action,
					inputConfiguration, createTrigger);

			inputConfiguration.putAll(edcOutput);

			Map<String, String> map = dftWorkFlow.getWorkFlow(customerDetails, action, inputConfiguration,
					createTrigger);

			// Send an email
			Map<String, Object> emailContent = new HashMap<>();
			
			emailContent.put("helloto", "Team");
			emailContent.put("orgname", customerDetails.getOrganizationName());
			emailContent.put("dftfrontendurl", map.get("dftfrontendurl"));
			emailContent.put("dftbackendurl", map.get("dftbackendurl"));
			emailContent.put("toemail", portalEmail);
			
			emailManager.sendEmail(emailContent, "DFT Application Deployed Successfully", "success.html");
			log.info("Email sent successfully");
			// End of email sending code

			String json = new ObjectMapper().writeValueAsString(extractResultMap(inputConfiguration));
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


	private void processDeleteTrigger(CustomerDetails customerDetails, String triggerId,
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

			AutoSetupTriggerEntry autoSetupTriggerEntry = autoSetupTriggerEntryRepository
					.findTop1ByBpnNumberAndStatusOrderByCreatedTimestampDesc(
							dftUpdateRequest.getBpnNumber(), "MANUAL_UPDATE_PENDING");

			if (autoSetupTriggerEntry != null) {

				ObjectMapper mapper = new ObjectMapper();

				CustomerDetails customerDetails = mapper.readValue(autoSetupTriggerEntry.getAutosetupRequest(),
						CustomerDetails.class);

				Map<String, String> inputConfiguration = prepareInputConfiguration(customerDetails);

				inputConfiguration.put("digital-twins.hostname", dftUpdateRequest.getDigitalTwinUrl());
				inputConfiguration.put("digital-twins.authentication.url", dftUpdateRequest.getDigitalTwinAuthUrl());
				inputConfiguration.put("digital-twins.authentication.clientId",
						dftUpdateRequest.getDigitalTwinClientId());
				inputConfiguration.put("digital-twins.authentication.clientSecret",
						dftUpdateRequest.getDigitalTwinClientSecret());

				inputConfiguration.put("dftkeycloakurl", dftUpdateRequest.getKeycloakUrl());
				inputConfiguration.put("dftcloakrealm", dftUpdateRequest.getKeycloakRealm());
				inputConfiguration.put("dftbackendkeycloakclientid", dftUpdateRequest.getKeycloakBackendClientId());
				inputConfiguration.put("dftfrontendkeycloakclientid", dftUpdateRequest.getKeycloakFrontendClientId());

				@SuppressWarnings("unchecked")
				Map<String, String> autosetupResult = new ObjectMapper()
						.readValue(autoSetupTriggerEntry.getAutosetupResult(), HashMap.class);
				inputConfiguration.putAll(autosetupResult);
				
				String controlService=  "http://" + customerDetails.getTenantName() + "edccontrolplane-edc-controlplane:8181/data";
				inputConfiguration.put("controlplaneservice", controlService);
				
				inputConfiguration.putAll(autosetupResult);

				Runnable runnable = () -> {
					
					manualPackageUpdate(customerDetails, inputConfiguration, autoSetupTriggerEntry);
					LocalDateTime now = LocalDateTime.now();
					autoSetupTriggerEntry.setModifiedTimestamp(now.toString());
					autoSetupTriggerEntry.setStatus(TriggerStatusEnum.SUCCESS.name());
					autoSetupTriggerManager.saveTriggerUpdate(autoSetupTriggerEntry);
				};

				new Thread(runnable).start();

				return autoSetupTriggerEntry.getTriggerId();
			}
			return "Autosetup create entry not present for manual update";
		} catch (Exception e) {
			return "Exception in manual updating dft package : " + e.getMessage();
		}

	}

	private void manualPackageUpdate(CustomerDetails customerDetails, Map<String, String> inputConfiguration,
			AutoSetupTriggerEntry createTrigger) {
		try {
			
			String tenantName = customerDetails.getTenantName();
			appManagement.deletePackage(POSTGRES_DB, tenantName + "dft", inputConfiguration);
			appManagement.deletePackage(DFT_BACKEND, tenantName, inputConfiguration);
			appManagement.deletePackage(DFT_FRONTEND, tenantName, inputConfiguration);
			
			//Sleep thread to wait for existing package deletetion
			log.info("Waiting after deleteing DFT packages");
			Thread.sleep(5000);
			
			Map<String, String> map = dftWorkFlow.getWorkFlow(customerDetails, CREATE, inputConfiguration,
					createTrigger);

			String json = new ObjectMapper().writeValueAsString(extractResultMap(inputConfiguration));
			createTrigger.setAutosetupResult(json);

			// Send an email
			Map<String, Object> emailContent = new HashMap<>();
			emailContent.put("orgname", customerDetails.getOrganizationName());
			emailContent.put("dftfrontendurl", map.get("dftfrontendurl"));
			emailContent.put("toemail", customerDetails.getEmail());
			emailContent.put("ccemail", portalEmail);
			
			emailManager.sendEmail(emailContent, "DFT Application Activited Successfully", "success_activate.html");
			log.info("Email sent successfully");
			// End of email sending code

			log.info("DFT Manual package update successfully!!!!");

		} catch (Exception e) {

			log.error("Error in DFT Manual package update " + e.getMessage());
			createTrigger.setStatus(TriggerStatusEnum.FAILED.name());
			createTrigger.setRemark(e.getMessage());
		}
	}

	private Map<String, String> prepareInputConfiguration(CustomerDetails customerDetails) {

		validateData(customerDetails);
		
		String targetNamespace = getTenantName(customerDetails);

		String dnsName = buildDnsName(targetNamespace);

		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();

		inputConfiguration.put("dnsName", dnsName);
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);
		inputConfiguration.put("bpnnumber", customerDetails.getBpnNumber());

		return inputConfiguration;
	}
	
	private Map<String, String> extractResultMap(Map<String, String> outputMap) {

		Map<String, String> resultMap = new ConcurrentHashMap<>();
		resultMap.put("dftfrontendurl", outputMap.get("dftfrontendurl"));
		resultMap.put("dftbackendurl", outputMap.get("dftbackendurl"));
		resultMap.put("controlplaneendpoint", outputMap.get("controlplaneendpoint"));
		resultMap.put("controlplanedataendpoint", outputMap.get("controlplanedataendpoint"));
		resultMap.put("dataplanepublicendpoint", outputMap.get("dataplanepublicendpoint"));
		resultMap.put("edcapi-key", outputMap.get("edcapi-key"));
		resultMap.put("edcapi-key-value", outputMap.get("edcapi-key-value"));

		return resultMap;
	}

	private String buildDnsName(String targetNamespace) {
		return dnsOriginalName.replace("tenantname", targetNamespace);
	}
	
	private void validateData(CustomerDetails customerDetails) {

		if (StringUtils.isEmpty(customerDetails.getOrganizationName()))
			throw new ValidationException("Organization name should not be null or empty");
		
		if (StringUtils.isEmpty(customerDetails.getEmail()))
			throw new ValidationException("Email should not be null or empty");

	}
	
	private String getTenantName(CustomerDetails customerDetails) {

		String orgName = customerDetails.getOrganizationName();
		int tenantNameLength = 6;
		String tenantName = orgName.replaceAll("[^a-zA-Z0-9]", "");
		tenantName = tenantName.length() < tenantNameLength ? tenantName : tenantName.substring(0, tenantNameLength);

		customerDetails.setTenantName(tenantName.toLowerCase());
		
		return customerDetails.getTenantName();
	}
}
