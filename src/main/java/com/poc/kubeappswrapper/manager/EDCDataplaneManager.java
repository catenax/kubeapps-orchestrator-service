package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EDCDataplaneManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	public Map<String, String> managePackage(CustomerDetails customerDetails, AppActions action,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString())
				.step(EDC_DATAPLANE.name())
				.triggerIdforinsert(triger.getTriggerId())
				.build();
		try {

			String dnsName = inputData.get("dnsName");

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(EDC_DATAPLANE, customerDetails.getTenantName(), inputData);
			else
				appManagement.updatePackage(EDC_DATAPLANE, customerDetails.getTenantName(), inputData);

			String dataplaneurl = "http://" + dnsName + "/" + customerDetails.getTenantName()
					+ "edcdataplane/api/public";

			inputData.put("dataplanepublicendpoint", dataplaneurl);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());

			throw new ServiceException("EDCDataplaneManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return inputData;
	}
}
