package com.poc.kubeappswrapper.proxy.portal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PortalIntegrationProxy", url = "${portal.url}", configuration = PortalIntegrationConfiguration.class)
public interface  PortalIntegrationProxy {
	
	@GetMapping(path = "/portal/getDigitaltwinandKeyclockdetails")
	String getDigitaltwinandKeyclockdetails(@RequestParam String dftFrontendUrl);

}
