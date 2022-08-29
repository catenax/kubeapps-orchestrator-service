package com.poc.kubeappswrapper.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.manager.CertificateManager;
import com.poc.kubeappswrapper.manager.DAPsManager;
import com.poc.kubeappswrapper.manager.EDCControlplaneManager;
import com.poc.kubeappswrapper.manager.EDCDataplaneManager;
import com.poc.kubeappswrapper.manager.PostgresDBManager;
import com.poc.kubeappswrapper.manager.VaultManager;
import com.poc.kubeappswrapper.model.CustomerDetails;

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

	public Map<String, String> getWorkFlow(CustomerDetails customerDetails,
			AppActions workflowAction, Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		inputConfiguration.putAll(certificateManager.createCertificate(customerDetails, inputConfiguration, triger));
		inputConfiguration.putAll(dapsManager.registerClientInDAPs(customerDetails, inputConfiguration, triger));
		inputConfiguration.putAll(vaultManager.uploadKeyandValues(customerDetails, inputConfiguration, triger));
		inputConfiguration.putAll(postgresManager.managePackage(customerDetails, workflowAction, "edc", inputConfiguration, triger));
		inputConfiguration.putAll(edcControlplaneManager.managePackage(customerDetails, workflowAction, inputConfiguration, triger));
		inputConfiguration.putAll(edcDataplaneManager.managePackage(customerDetails, workflowAction, inputConfiguration, triger));
		
		return inputConfiguration;
	}
}
