package com.poc.kubeappswrapper.manager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.proxy.portal.PortalIntegrationProxy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortalIntegrationManager {

	private final PortalIntegrationProxy portalIntegrationProxy;

	public Map<String, String> getDigitalandKeyCloackDetails(CustomerDetails customerDetails,
			Map<String, String> inputData) {

		//String dftfrontendUrl = inputData.get("dftfrontendurl");
		
		//String digitaltwinandKeyclockdetails = portalIntegrationProxy.getDigitaltwinandKeyclockdetails(dftfrontendUrl);

		inputData.put("digital-twins.hostname", "https://semantics.dev.demo.catena-x.net");
		inputData.put("digital-twins.authentication.url",
				"https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/token");
		inputData.put("digital-twins.authentication.clientId", "sa-cl6-cx-17");
		inputData.put("digital-twins.authentication.clientSecret", "Fc82eBzxmqSGkmRykBwqRdoYiJ3xVFyy");

		inputData.put("dftkeyclockurl", "");
		inputData.put("dftkeyclockrealm", "");
		inputData.put("dftkeyclockclientid", "");

		return inputData;
	}

}
