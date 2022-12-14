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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.keycloak.OAuth2Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.catenax.autosetup.portal.model.ClientInfo;
import net.catenax.autosetup.portal.model.ServiceInstanceResultRequest;
import net.catenax.autosetup.portal.model.ServiceInstanceResultResponse;
import net.catenax.autosetup.portal.model.TechnicalUserInfo;
import net.catenax.autosetup.portal.proxy.PortalIntegrationProxy;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortalIntegrationManager {

	private final PortalIntegrationProxy portalIntegrationProxy;

	@Value("${portal.url}")
	private URI portalUrl;

	@Value("${portal.keycloak.clientId}")
	private String clientId;

	@Value("${portal.keycloak.clientSecret}")
	private String clientSecret;

	@Value("${portal.keycloak.tokenURI}")
	private URI tokenURI;

	@Value("${portal.dft.keycloak.realm}")
	private String keycloakRealm;

	@Value("${portal.dft.keycloak.clientId}")
	private String keycloakClientId;

	@Value("${portal.dft.keycloak.url}")
	private String keycloakUrl;

	@Value("${portal.dft.digitalTwinUrl}")
	private String digitalTwinUrl;

	@Value("${portal.dft.digitalTwinAuthUrl}")
	private String digitalTwinAuthUrl;

	@SneakyThrows
	public Map<String, String> postServiceInstanceResultAndGetTenantSpecs(Map<String, String> inputData) {

		String dftFrontendURL = inputData.get("dftFrontEndUrl");
		String subscriptionId = inputData.get("subscriptionId");

		Map<String, String> header = new HashMap<>();
		header.put("Authorization", "Bearer " + getKeycloakToken());

		ServiceInstanceResultRequest serviceInstanceResultRequest = ServiceInstanceResultRequest.builder()
				.requestId(subscriptionId).offerUrl(dftFrontendURL).build();

		ServiceInstanceResultResponse serviceInstanceResultResponse = portalIntegrationProxy
				.postServiceInstanceResultAndGetTenantSpecs(portalUrl, header, serviceInstanceResultRequest);

		if (serviceInstanceResultResponse != null) {
			inputData.put("digital-twins.hostname", digitalTwinUrl);
			inputData.put("digital-twins.authentication.url", digitalTwinAuthUrl);

			TechnicalUserInfo technicalUserInfo = serviceInstanceResultResponse.getTechnicalUserInfo();
			if (technicalUserInfo != null) {
				inputData.put("digital-twins.authentication.clientId", technicalUserInfo.getTechnicalClientId());
				inputData.put("digital-twins.authentication.clientSecret", technicalUserInfo.getTechnicalUserSecret());
			}

			inputData.put("dftkeycloakurl", keycloakUrl);
			inputData.put("dftcloakrealm", keycloakRealm);

			ClientInfo clientInfo = serviceInstanceResultResponse.getClientInfo();

			if (clientInfo != null) {
				inputData.put("dftbackendkeycloakclientid", clientInfo.getClientId());
				inputData.put("dftfrontendkeycloakclientid", clientInfo.getClientId());
			}
		} else {
			log.error("Error in request process with portal");
		}

		return inputData;
	}

	@SneakyThrows
	private String getKeycloakToken() {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS);
		body.add(OAuth2Constants.CLIENT_ID, clientId);
		body.add(OAuth2Constants.CLIENT_SECRET, clientSecret);
		var resultBody = portalIntegrationProxy.readAuthToken(tokenURI, body);

		if (resultBody != null) {
			return resultBody.getAccessToken();
		}
		return null;

	}

}
