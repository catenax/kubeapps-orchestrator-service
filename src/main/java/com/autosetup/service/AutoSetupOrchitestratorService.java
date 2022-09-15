package com.autosetup.service;

import static com.autosetup.constant.AppActions.CREATE;
import static com.autosetup.constant.AppActions.DELETE;
import static com.autosetup.constant.AppActions.UPDATE;
import static com.autosetup.constant.AppNameConstant.DFT_BACKEND;
import static com.autosetup.constant.AppNameConstant.DFT_FRONTEND;
import static com.autosetup.constant.AppNameConstant.EDC_CONTROLPLANE;
import static com.autosetup.constant.AppNameConstant.EDC_DATAPLANE;
import static com.autosetup.constant.AppNameConstant.POSTGRES_DB;
import static com.autosetup.constant.TriggerStatusEnum.FAILED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.autosetup.constant.AppActions;
import com.autosetup.constant.ToolType;
import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.NoDataFoundException;
import com.autosetup.exception.ValidationException;
import com.autosetup.kubeapp.mapper.AutoSetupRequestMapper;
import com.autosetup.kubeapp.mapper.AutoSetupTriggerMapper;
import com.autosetup.manager.AutoSetupTriggerManager;
import com.autosetup.manager.EmailManager;
import com.autosetup.manager.InputConfigurationManager;
import com.autosetup.manager.KubeAppsPackageManagement;
import com.autosetup.manager.ManualDFTPackageUpdateManager;
import com.autosetup.model.AutoSetupRequest;
import com.autosetup.model.Customer;
import com.autosetup.model.DFTUpdateRequest;
import com.autosetup.model.SelectedTools;
import com.autosetup.proxy.kubeapps.KubeAppManageProxy;
import com.autosetup.repository.AutoSetupTriggerEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoSetupOrchitestratorService {

	private final KubeAppManageProxy kubeAppManageProxy;
	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final EDCConnectorWorkFlow edcConnectorWorkFlow;
	private final DFTAppWorkFlow dftWorkFlow;

	private final InputConfigurationManager inputConfigurationManager;
	private final ManualDFTPackageUpdateManager manualDFTPackageUpdateManager;

	private final AutoSetupTriggerMapper autoSetupTriggerMapper;
	private final AutoSetupRequestMapper autoSetupRequestMapper;

	@Autowired
	private AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;

	@Autowired
	private EmailManager emailManager;

	@Value("${target.cluster}")
	private String targetCluster;

	@Value("${portal.email.address}")
	private String portalEmail;

	public String getAllInstallPackages() {
		return kubeAppManageProxy.getAllInstallPackages();
	}

	public String createPackage(AutoSetupRequest autoSetupRequest) {

		String uuID = UUID.randomUUID().toString();

		String organizationName = autoSetupRequest.getCustomer().getOrganizationName();
		AutoSetupTriggerEntry checkTrigger = autoSetupTriggerManager
				.isAutoSetupAvailableforOrgnizationName(organizationName);

		if (checkTrigger != null && !DELETE.name().equals(checkTrigger.getTriggerType())) {
			throw new ValidationException("Auto setup already exist for " + organizationName
					+ ", use execution id to update it " + checkTrigger.getTriggerId());
		}

		Runnable runnable = () -> {

			packageNaming(autoSetupRequest);

			Map<String, String> inputConfiguration = inputConfigurationManager
					.prepareInputConfiguration(autoSetupRequest.getCustomer(), uuID);

			String targetNamespace = inputConfiguration.get("targetNamespace");

			AutoSetupTriggerEntry trigger = autoSetupTriggerManager.createTrigger(autoSetupRequest, CREATE, uuID,
					targetNamespace);

			if (!checkNamespaceisExist(targetNamespace)) {
				kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);
			}

			proceessTrigger(autoSetupRequest, CREATE, trigger, inputConfiguration);
		};

		new Thread(runnable).start();

		return uuID;
	}

	public String updatePackage(AutoSetupRequest autoSetupRequest, String triggerId) {

		AutoSetupTriggerEntry trigger = autoSetupTriggerEntryRepository.findAllByTriggerId(triggerId);

		if (trigger != null && !FAILED.name().equals(trigger.getStatus())) {

			Map<String, String> inputConfiguration = inputConfigurationManager
					.prepareInputConfiguration(autoSetupRequest.getCustomer(), triggerId);

			Runnable runnable = () -> {

				trigger.setAutosetupResult("");

				autoSetupTriggerManager.updateTriggerAutoSetupAsInProgress(trigger, UPDATE);

				String targetNamespace = inputConfiguration.get("targetNamespace");

				String existingNamespace = trigger.getAutosetupTenantName();

				if (checkNamespaceisExist(existingNamespace)) {

					inputConfiguration.put("targetNamespace", existingNamespace);
					
					processDeleteTrigger(trigger, inputConfiguration);

					inputConfiguration.put("targetNamespace", targetNamespace);
					trigger.setAutosetupTenantName(targetNamespace);
					
					if (!existingNamespace.equals(targetNamespace) && !checkNamespaceisExist(targetNamespace)) {
						kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);
					}
					try {
						log.info("Waiting after deleteing all package for recreate");
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}

				} else {
					trigger.setAutosetupTenantName(targetNamespace);
					kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);
				}

				packageNaming(autoSetupRequest);

				AutoSetupTriggerEntry updatedtrigger = autoSetupTriggerManager
						.updateTriggerAutoSetupRequest(autoSetupRequest, trigger, UPDATE);

				proceessTrigger(autoSetupRequest, CREATE, updatedtrigger, inputConfiguration);

			};

			new Thread(runnable).start();

		} else {
			throw new NoDataFoundException("No Valid Auto setup found for " + triggerId + " to update");
		}

		return triggerId;
	}

	public String deletePackage(String triggerId) {

		AutoSetupTriggerEntry trigger = autoSetupTriggerEntryRepository.findAllByTriggerId(triggerId);

		if (trigger != null) {

			Map<String, String> inputConfiguration = inputConfigurationManager.prepareInputFromDBObject(trigger);

			trigger.setAutosetupResult("");

			AutoSetupTriggerEntry deleteTrigger = autoSetupTriggerManager.updateTriggerAutoSetupAsInProgress(trigger,
					DELETE);

			Runnable runnable = () -> processDeleteTrigger(deleteTrigger, inputConfiguration);
			new Thread(runnable).start();
		} else {
			throw new NoDataFoundException("No Valid Auto setup found for " + triggerId + " to delete");
		}

		return triggerId;
	}

	private void proceessTrigger(AutoSetupRequest autoSetupRequest, AppActions action, AutoSetupTriggerEntry trigger,
			Map<String, String> inputConfiguration) {

		try {

			Customer customer = autoSetupRequest.getCustomer();

			trigger.setTriggerType(action.name());

			List<SelectedTools> edcToollist = autoSetupRequest.getSelectedTools().stream()
					.filter(e -> ToolType.EDC.equals(e.getTool())).toList();

			edcToollist.forEach((tool) -> {
				Map<String, String> edcOutput = edcConnectorWorkFlow.getWorkFlow(autoSetupRequest.getCustomer(), tool,
						action, inputConfiguration, trigger);
				inputConfiguration.putAll(edcOutput);
			});

			List<SelectedTools> dftToollist = autoSetupRequest.getSelectedTools().stream()
					.filter(e -> ToolType.DFT.equals(e.getTool())).toList();

			for (SelectedTools tool : dftToollist) {

				Map<String, String> map = dftWorkFlow.getWorkFlow(autoSetupRequest.getCustomer(), tool, action,
						inputConfiguration, trigger);

				// Send an email
				Map<String, Object> emailContent = new HashMap<>();

				emailContent.put("helloto", "Team");
				emailContent.put("orgname", customer.getOrganizationName());
				emailContent.put("dftFrontEndUrl", map.get("dftFrontEndUrl"));
				emailContent.put("dftBackEndUrl", map.get("dftBackEndUrl"));
				emailContent.put("toemail", portalEmail);

				// End of email sending code
				emailManager.sendEmail(emailContent, "DFT Application Deployed Successfully", "success.html");
				log.info("Email sent successfully");

				String json = autoSetupTriggerMapper.fromMaptoStr(extractResultMap(map));

				trigger.setAutosetupResult(json);

				trigger.setStatus(TriggerStatusEnum.MANUAL_UPDATE_PENDING.name());

				log.info("All Packages created/updated successfully!!!!");
			}

		} catch (Exception e) {

			log.error("Error in package creation " + e.getMessage());
			trigger.setAutosetupResult("");
			trigger.setStatus(TriggerStatusEnum.FAILED.name());
			trigger.setRemark(e.getMessage());

		}

		LocalDateTime now = LocalDateTime.now();
		trigger.setModifiedTimestamp(now.toString());

		autoSetupTriggerManager.saveTriggerUpdate(trigger);
	}

	private void processDeleteTrigger(AutoSetupTriggerEntry trigger, Map<String, String> inputConfiguration) {

		AutoSetupRequest autoSetupRequest = autoSetupRequestMapper.fromStr(trigger.getAutosetupRequest());

		List<SelectedTools> edcToollist = autoSetupRequest.getSelectedTools().stream()
				.filter(e -> ToolType.EDC.equals(e.getTool())).toList();

		edcToollist.forEach((tool) -> {
			appManagement.deletePackage(POSTGRES_DB, tool.getPackageName(), inputConfiguration);
			appManagement.deletePackage(EDC_CONTROLPLANE, tool.getPackageName(), inputConfiguration);
			appManagement.deletePackage(EDC_DATAPLANE, tool.getPackageName(), inputConfiguration);
		});

		List<SelectedTools> dftToollist = autoSetupRequest.getSelectedTools().stream()
				.filter(e -> ToolType.DFT.equals(e.getTool())).toList();

		for (SelectedTools tool : dftToollist) {
			appManagement.deletePackage(POSTGRES_DB, tool.getPackageName(), inputConfiguration);
			appManagement.deletePackage(DFT_BACKEND, tool.getPackageName(), inputConfiguration);
			appManagement.deletePackage(DFT_FRONTEND, tool.getPackageName(), inputConfiguration);
		}

		trigger.setStatus(TriggerStatusEnum.SUCCESS.name());

		autoSetupTriggerManager.saveTriggerUpdate(trigger);

		log.info("All Packages deleted successfully!!!!");
	}

	@SneakyThrows
	public String updateDftPackage(String triggerId, DFTUpdateRequest dftUpdateRequest) {

		AutoSetupTriggerEntry trigger = autoSetupTriggerEntryRepository.findAllByTriggerId(triggerId);

		if (trigger != null && TriggerStatusEnum.MANUAL_UPDATE_PENDING.name().equals(trigger.getStatus())) {

			AutoSetupRequest autosetupRequest = autoSetupRequestMapper.fromStr(trigger.getAutosetupRequest());

			Map<String, String> inputConfiguration = inputConfigurationManager
					.prepareInputConfiguration(autosetupRequest.getCustomer(), triggerId);

			Runnable runnable = () -> {

				try {

					Map<String, String> output = manualDFTPackageUpdateManager.manualPackageUpdate(autosetupRequest,
							dftUpdateRequest, inputConfiguration, trigger);

					String json = autoSetupTriggerMapper.fromMaptoStr(extractResultMap(output));

					trigger.setAutosetupResult(json);

					trigger.setStatus(TriggerStatusEnum.SUCCESS.name());

				} catch (Exception e) {

					log.error("Error in manual package updation " + e.getMessage());
					trigger.setStatus(TriggerStatusEnum.FAILED.name());
					trigger.setRemark(e.getMessage());
					trigger.setAutosetupResult("");
				}

				LocalDateTime now = LocalDateTime.now();
				trigger.setModifiedTimestamp(now.toString());
				autoSetupTriggerManager.saveTriggerUpdate(trigger);

			};
			new Thread(runnable).start();

			return trigger.getTriggerId();
		} else {
			throw new NoDataFoundException("Autosetup entry not present for manual update");
		}

	}

	private List<Map<String, String>> extractResultMap(Map<String, String> outputMap) {

		List<Map<String, String>> processResult = new ArrayList<>();

		Map<String, String> dft = new ConcurrentHashMap<>();
		dft.put("name", "DFT");
		dft.put("dftFrontEndUrl", outputMap.get("dftFrontEndUrl"));
		dft.put("dftBackEndUrl", outputMap.get("dftBackEndUrl"));
		processResult.add(dft);

		Map<String, String> edc = new ConcurrentHashMap<>();
		edc.put("name", "EDC");
		edc.put("controlPlaneEndpoint", outputMap.get("controlPlaneEndpoint"));
		edc.put("controlPlaneDataEndpoint", outputMap.get("controlPlaneDataEndpoint"));
		edc.put("dataPlanePublicEndpoint", outputMap.get("dataPlanePublicEndpoint"));
		edc.put("edcApiKey", outputMap.get("edcApiKey"));
		edc.put("edcApiKeyValue", outputMap.get("edcApiKeyValue"));
		processResult.add(edc);

		return processResult;
	}

	private void packageNaming(AutoSetupRequest autoSetupRequest) {

		Map<String, Integer> internalMap = new TreeMap<>();

		for (SelectedTools elelment : autoSetupRequest.getSelectedTools()) {

			int count = 1;
			String name = elelment.getTool().name();

			if (internalMap.containsKey(name)) {
				count = internalMap.get(name) + 1;
			}

			internalMap.put(name, count);

			int tenantNameLength = 15;
			String label = elelment.getLabel();

			label = label.length() < tenantNameLength ? label : label.substring(0, tenantNameLength);

			elelment.setPackageName(label.concat("-" + count));

		}

	}

	private boolean checkNamespaceisExist(String targetNamespace) {

		String namespacesResult = kubeAppManageProxy.checkNamespace(targetCluster, targetNamespace);
		return namespacesResult.contains("true");
	}
}
