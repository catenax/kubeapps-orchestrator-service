/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.autosetup.service;

import static net.catenax.autosetup.constant.AppNameConstant.DFT_BACKEND;
import static net.catenax.autosetup.constant.AppNameConstant.DFT_FRONTEND;
import static net.catenax.autosetup.constant.AppNameConstant.POSTGRES_DB;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.catenax.autosetup.constant.AppActions;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.manager.AppDeleteManager;
import net.catenax.autosetup.manager.DFTBackendManager;
import net.catenax.autosetup.manager.DFTFrontendManager;
import net.catenax.autosetup.manager.PostgresDBManager;
import net.catenax.autosetup.model.Customer;
import net.catenax.autosetup.model.SelectedTools;

@Component
@RequiredArgsConstructor
public class DFTAppWorkFlow {

	private final PostgresDBManager postgresManager;
	private final DFTBackendManager dftBackendManager;
	private final DFTFrontendManager dftFrontendManager;

	private final AppDeleteManager appDeleteManager;

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

	public void deletePackageWorkFlow(SelectedTools tool, Map<String, String> inputConfiguration,
			AutoSetupTriggerEntry triger) {

		appDeleteManager.deletePackage(POSTGRES_DB, tool, inputConfiguration, triger);
		appDeleteManager.deletePackage(DFT_BACKEND, tool, inputConfiguration, triger);
		appDeleteManager.deletePackage(DFT_FRONTEND, tool, inputConfiguration, triger);

	}
}
