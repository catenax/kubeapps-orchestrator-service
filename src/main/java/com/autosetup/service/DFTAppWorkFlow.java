package com.autosetup.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.autosetup.constant.AppActions;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.manager.DFTBackendManager;
import com.autosetup.manager.DFTFrontendManager;
import com.autosetup.manager.PostgresDBManager;
import com.autosetup.model.Customer;
import com.autosetup.model.SelectedTools;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DFTAppWorkFlow {

	private final PostgresDBManager postgresManager;
	private final DFTBackendManager dftBackendManager;
	private final DFTFrontendManager dftFrontendManager;

	public Map<String, String> getWorkFlow(Customer customerDetails, SelectedTools tool, AppActions workflowAction,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		inputConfiguration.putAll(
				postgresManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));
		inputConfiguration.putAll(
				dftBackendManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));
		inputConfiguration.putAll(
				dftFrontendManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));

		return inputConfiguration;
	}
}
