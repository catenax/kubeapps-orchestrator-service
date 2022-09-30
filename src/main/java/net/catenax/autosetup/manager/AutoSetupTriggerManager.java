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

package net.catenax.autosetup.manager;

import static net.catenax.autosetup.constant.TriggerStatusEnum.FAILED;
import static net.catenax.autosetup.constant.TriggerStatusEnum.INPROGRESS;
import static net.catenax.autosetup.constant.TriggerStatusEnum.SUCCESS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.catenax.autosetup.constant.AppActions;
import net.catenax.autosetup.entity.AutoSetupTriggerDetails;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.exception.NoDataFoundException;
import net.catenax.autosetup.mapper.AutoSetupRequestMapper;
import net.catenax.autosetup.mapper.AutoSetupTriggerMapper;
import net.catenax.autosetup.model.AutoSetupRequest;
import net.catenax.autosetup.model.AutoSetupResponse;
import net.catenax.autosetup.model.AutoSetupTriggerResponse;
import net.catenax.autosetup.model.Customer;
import net.catenax.autosetup.model.CustomerProperties;
import net.catenax.autosetup.repository.AutoSetupTriggerEntryRepository;

@Service
@RequiredArgsConstructor
public class AutoSetupTriggerManager {

	private final AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;
	private final AutoSetupRequestMapper customerDetailsMapper;
	private final AutoSetupTriggerMapper autoSetupTriggerMapper;

	public AutoSetupTriggerEntry createTrigger(AutoSetupRequest autoSetupRequest, AppActions action, String triggerId,
			String tenantNamespace) {
		LocalDateTime now = LocalDateTime.now();
		Customer customer = autoSetupRequest.getCustomer();
		CustomerProperties customerProp = autoSetupRequest.getProperties();
		AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
				.organizationName(customer.getOrganizationName())
				.subscriptionId(customerProp.getSubscriptionId())
				.serviceId(customerProp.getServiceId())
				.autosetupRequest(customerDetailsMapper.fromCustomer(autoSetupRequest)).triggerId(triggerId)
				.triggerType(action.name()).createdTimestamp(now.toString()).modifiedTimestamp(now.toString())
				.status(INPROGRESS.name()).autosetupTenantName(tenantNamespace).build();

		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry saveTriggerUpdate(AutoSetupTriggerEntry autoSetupTriggerEntry) {
		LocalDateTime now = LocalDateTime.now();
		autoSetupTriggerEntry.setModifiedTimestamp(now.toString());
		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry updateTriggerAutoSetupRequest(AutoSetupRequest autoSetupRequest,
			AutoSetupTriggerEntry autoSetupTriggerEntry, AppActions action) {

		autoSetupTriggerEntry.setTriggerType(action.name());
		autoSetupTriggerEntry.setOrganizationName(autoSetupRequest.getCustomer().getOrganizationName());
		autoSetupTriggerEntry.setAutosetupRequest(customerDetailsMapper.fromCustomer(autoSetupRequest));
		autoSetupTriggerEntry.setStatus(INPROGRESS.name());
		LocalDateTime now = LocalDateTime.now();
		autoSetupTriggerEntry.setModifiedTimestamp(now.toString());

		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerDetails saveTriggerDetails(AutoSetupTriggerDetails autoSetupTriggerDetails,
			AutoSetupTriggerEntry trigger) {
		autoSetupTriggerDetails.setCreatedDate(LocalDateTime.now());
		autoSetupTriggerDetails.setAction(trigger.getTriggerType());
		trigger.addTriggerDetails(autoSetupTriggerDetails);
		autoSetupTriggerEntryRepository.save(trigger);
		return autoSetupTriggerDetails;
	}

	public List<AutoSetupTriggerResponse> getAllTriggers() {
		return Optional.of(autoSetupTriggerEntryRepository.findAll()).orElseGet(ArrayList::new).stream()
				.map(autoSetupTriggerMapper::fromEntitytoCustom).toList();
	}

	public AutoSetupTriggerResponse getTriggerDetails(String triggerId) {
		return autoSetupTriggerEntryRepository.findById(triggerId)
				.map(autoSetupTriggerMapper::fromEntitytoCustom)
				.orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

	}

	public AutoSetupResponse getCheckDetails(String triggerId) {

		return Optional.ofNullable(autoSetupTriggerEntryRepository.findAllByTriggerId(triggerId)).map(obj -> {
			AutoSetupResponse newobj = autoSetupTriggerMapper.fromEntitytoAutoSetupCustom(obj);

			newobj.setRemark(null);
			if (FAILED.name().equals(obj.getStatus())) {
				newobj.setRemark("Please connect with technical team for more advice");
			}

			if (!SUCCESS.name().equals(obj.getStatus())) {
				newobj.setProcessResult(List.of());
			}
			return newobj;

		}).orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

	}

	public AutoSetupTriggerEntry isAutoSetupAvailableforOrgnizationName(String organizationName) {
		return autoSetupTriggerEntryRepository.findTop1ByOrganizationName(organizationName);
	}

}
