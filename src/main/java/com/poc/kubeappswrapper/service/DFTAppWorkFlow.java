package com.poc.kubeappswrapper.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.manager.DFTBackendManager;
import com.poc.kubeappswrapper.manager.DFTFrontendManager;
import com.poc.kubeappswrapper.manager.PostgresDBManager;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DFTAppWorkFlow {

	private final PostgresDBManager postgresManager;
	private final DFTBackendManager dftBackendManager;
	private final DFTFrontendManager dftFrontendManager;

	public Map<String, String> getWorkFlow(CustomerDetails customerDetails,
			AppActions workflowAction, Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		
		inputConfiguration.putAll(postgresManager.managePackage(customerDetails, workflowAction, "dft", inputConfiguration, triger));
		inputConfiguration.putAll(dftBackendManager.managePackage(customerDetails, workflowAction, inputConfiguration, triger));
		inputConfiguration.putAll(dftFrontendManager.managePackage(customerDetails, workflowAction, inputConfiguration, triger));
		
		
		return inputConfiguration;
	}
}
