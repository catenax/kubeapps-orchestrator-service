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

package com.autosetup.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "auto_setup_trigger_tbl")
@JsonInclude(Include.NON_NULL)
public class AutoSetupTriggerEntry {

	@Id
	private String triggerId;

	private String triggerType;

	private String organizationName;

	@JsonIgnore
	private String autosetupTenantName;

	@OneToMany(targetEntity = AutoSetupTriggerDetails.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "trigger_id", referencedColumnName = "triggerId")
	private List<AutoSetupTriggerDetails> autosetupTriggerDetails;

	@Column(name = "autosetup_request", columnDefinition = "LONGTEXT")
	private String autosetupRequest;

	@Column(name = "autosetup_result", columnDefinition = "LONGTEXT")
	private String autosetupResult;

	private String createdTimestamp;

	private String modifiedTimestamp;

	private String status;

	@Column(name = "remark", columnDefinition = "LONGTEXT")
	private String remark;

	public void addTriggerDetails(AutoSetupTriggerDetails autoSetupTriggerDetails) {
		if (autosetupTriggerDetails == null)
			autosetupTriggerDetails = new ArrayList<>();
		autosetupTriggerDetails.add(autoSetupTriggerDetails);
	}
}
