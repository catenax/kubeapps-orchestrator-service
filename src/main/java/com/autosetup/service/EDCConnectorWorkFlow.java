package com.autosetup.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.autosetup.constant.AppActions;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.manager.CertificateManager;
import com.autosetup.manager.DAPsManager;
import com.autosetup.manager.EDCControlplaneManager;
import com.autosetup.manager.EDCDataplaneManager;
import com.autosetup.manager.PostgresDBManager;
import com.autosetup.manager.VaultManager;
import com.autosetup.model.Customer;
import com.autosetup.model.SelectedTools;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EDCConnectorWorkFlow {

	private final CertificateManager certificateManager;
	private final DAPsManager dapsManager;
	private final VaultManager vaultManager;
	private final PostgresDBManager postgresManager;
	private final EDCControlplaneManager edcControlplaneManager;
	private final EDCDataplaneManager edcDataplaneManager;

	public Map<String, String> getWorkFlow(Customer customerDetails, SelectedTools tool, AppActions workflowAction,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		inputConfiguration
				.putAll(certificateManager.createCertificate(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(dapsManager.registerClientInDAPs(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(vaultManager.uploadKeyandValues(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(
				postgresManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));
		inputConfiguration.putAll(edcControlplaneManager.managePackage(customerDetails, workflowAction, tool,
				inputConfiguration, triger));
		inputConfiguration.putAll(
				edcDataplaneManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));

		return inputConfiguration;
	}
}
