package com.autosetup.service;

import java.util.Map;

import com.autosetup.manager.*;
import org.springframework.stereotype.Component;

import com.autosetup.constant.AppActions;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.model.Customer;
import com.autosetup.model.SelectedTools;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EDCConnectorWorkFlow {

	private final CertificateManager certificateManager;
	private final DAPsWrapperManager daPsWrapperManager;
	private final VaultManager vaultManager;
	private final PostgresDBManager postgresManager;
	private final EDCControlplaneManager edcControlplaneManager;
	private final EDCDataplaneManager edcDataplaneManager;

	public Map<String, String> getWorkFlow(Customer customerDetails, SelectedTools tool, AppActions workflowAction,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		inputConfiguration
				.putAll(certificateManager.createCertificate(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(daPsWrapperManager.createClient(customerDetails, tool, inputConfiguration, triger));
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
