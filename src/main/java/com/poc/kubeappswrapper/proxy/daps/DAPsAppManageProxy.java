package com.poc.kubeappswrapper.proxy.daps;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.poc.kubeappswrapper.model.DAPsClientCertificateRequest;
import com.poc.kubeappswrapper.model.DAPsClientRequest;
import com.poc.kubeappswrapper.model.DAPsTokenResponse;

@FeignClient(name = "DAPSAppManageProxy", url = "${daps.url}", configuration = DAPsConfiguration.class)
public interface DAPsAppManageProxy {

	@PostMapping(path = "/token")
	DAPsTokenResponse readAuthToken(@RequestParam("grant_type") String grant_type,
			@RequestParam("client_id") String client_id, @RequestParam("client_secret") String client_secret,
			@RequestParam("scope") String scope);

	@PostMapping(path = "/api/v1/config/clients")
	String createClient(@RequestBody DAPsClientRequest dapsClientRequest,
			@RequestHeader Map<String, String> requestHeader);

	@PostMapping(path = "/api/v1/config/clients/{clientId}/keys")
	String uploadClientCertificate(@PathVariable("clientId") String clientId,
			@RequestBody DAPsClientCertificateRequest dapsClientCertificate,
			@RequestHeader Map<String, String> requestHeader);

}
