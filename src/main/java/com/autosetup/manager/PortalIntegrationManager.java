package com.autosetup.manager;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.autosetup.model.Customer;
import com.autosetup.proxy.portal.PortalIntegrationProxy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortalIntegrationManager {

	private final PortalIntegrationProxy portalIntegrationProxy;

	public Map<String, String> getDigitalandKeyCloackDetails(Customer customerDetails,
			Map<String, String> inputData) {

		// String dftFrontEndUrl = inputData.get("dftFrontEndUrl");

		// String digitaltwinandkeycloakdetails =
		// portalIntegrationProxy.getDigitaltwinandkeycloakdetails(dftfrontendUrl);

//		inputData.put("digital-twins.hostname", "https://semantics.dev.demo.catena-x.net");
//		inputData.put("digital-twins.authentication.url",
//				"https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/token");
//		inputData.put("digital-twins.authentication.clientId", "sa-cl6-cx-17");
//		inputData.put("digital-twins.authentication.clientSecret", "Fc82eBzxmqSGkmRykBwqRdoYiJ3xVFyy");

//		inputData.put("dftkeycloakurl", dftUpdateRequest.getKeycloakUrl());
//		inputData.put("dftcloakrealm", dftUpdateRequest.getKeycloakRealm());
//		inputData.put("dftbackendkeycloakclientid", dftUpdateRequest.getKeycloakBackendClientId());
//		inputData.put("dftfrontendkeycloakclientid", dftUpdateRequest.getKeycloakFrontendClientId());


		return inputData;
	}

}
