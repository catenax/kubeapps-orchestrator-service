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

package com.autosetup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DAPsClientRequest {
	@JsonProperty("client_id")
	private String clientId;
	private String name;
	@JsonProperty("token_endpoint_auth_method")
	private String tokenEndpointAuthMethod;
	private List<String> scope;
	@JsonProperty("grant_types")
	private List<String> grantTypes;
	private List<Attribute> attributes;
	
}
