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

package com.autosetup.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;

@Repository
public class AutoSetupTriggerCustomRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public AutoSetupTriggerDetails save(AutoSetupTriggerDetails autoSetupTriggerDetails) {
		try {
			jdbcTemplate.update(
					"insert into auto_setup_trigger_details_tbl (id, trigger_id, step, status, remark, created_date) values(?,?,?,?,?,?)",
					autoSetupTriggerDetails.getId(), autoSetupTriggerDetails.getTriggerIdforinsert(),
					autoSetupTriggerDetails.getStep(), autoSetupTriggerDetails.getStatus(),
					autoSetupTriggerDetails.getRemark(), autoSetupTriggerDetails.getCreatedDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return autoSetupTriggerDetails;
	}

	public AutoSetupTriggerEntry saveTriggerUpdate(AutoSetupTriggerEntry autoSetupTriggerEntry) {
		try{
			jdbcTemplate.update(
					"UPDATE auto_setup_trigger_tbl set status=?, modified_timestamp=?, remark=? where status=? and organization_name=?",
					"CLOSED", autoSetupTriggerEntry.getModifiedTimestamp(), "Force close",
					TriggerStatusEnum.MANUAL_UPDATE_PENDING.name(), autoSetupTriggerEntry.getOrganizationName());

			jdbcTemplate.update(
					"UPDATE auto_setup_trigger_tbl set status=? ,autosetup_result=?, modified_timestamp=?, remark=? where trigger_id=?",
					autoSetupTriggerEntry.getStatus(), autoSetupTriggerEntry.getAutosetupResult(),
					autoSetupTriggerEntry.getModifiedTimestamp(), autoSetupTriggerEntry.getRemark(),
					autoSetupTriggerEntry.getTriggerId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return autoSetupTriggerEntry;
	}

	public AutoSetupTriggerEntry updateTriggerAutoSetupRequest(AutoSetupTriggerEntry autoSetupTriggerEntry) {
		try {
			jdbcTemplate.update(
					"UPDATE auto_setup_trigger_tbl set status=?, organization_name=?, autosetup_tenant_name=?, trigger_type=? ,autosetup_request=?, modified_timestamp=? where trigger_id=?",
					autoSetupTriggerEntry.getStatus(), autoSetupTriggerEntry.getOrganizationName(),
					autoSetupTriggerEntry.getAutosetupTenantName(), autoSetupTriggerEntry.getTriggerType(),
					autoSetupTriggerEntry.getAutosetupRequest(), autoSetupTriggerEntry.getModifiedTimestamp(),
					autoSetupTriggerEntry.getTriggerId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return autoSetupTriggerEntry;
	}

}
