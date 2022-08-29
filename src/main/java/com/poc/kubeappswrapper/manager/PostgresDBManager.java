package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.POSTGRES_DB;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.utility.PasswordGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostgresDBManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action, String packagefor,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString())
				.step(POSTGRES_DB.name()+""+packagefor)
				.triggerIdforinsert(triger.getTriggerId())
				.build();
		try {

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
			inputData.put("postgresPassword", generateRandomPassword);
			inputData.put("username", "admin");
			inputData.put("password", generateRandomPassword);
			inputData.put("database", "postgres");

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(POSTGRES_DB, customerDetails.getTenantName() + "" + packagefor, inputData);
			else
				appManagement.updatePackage(POSTGRES_DB, customerDetails.getTenantName() + "" + packagefor, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("PostgresDBManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return inputData;
	}

}
