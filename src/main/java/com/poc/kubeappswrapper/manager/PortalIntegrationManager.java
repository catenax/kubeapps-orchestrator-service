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

		Map<String, String> outputData = new HashMap<>();
		outputData.put("digital-twins.hostname", "https://semantics.dev.demo.catena-x.net");
		outputData.put("digital-twins.authentication.url",
				"https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/token");
		outputData.put("digital-twins.authentication.clientId", "sa-cl6-cx-17");
		outputData.put("digital-twins.authentication.clientSecret", "Fc82eBzxmqSGkmRykBwqRdoYiJ3xVFyy");

		outputData.put("dftkeyclockurl", "");
		outputData.put("dftkeyclockrealm", "");
		outputData.put("dftkeyclockclientid", "");

		return outputData;
	}

}
