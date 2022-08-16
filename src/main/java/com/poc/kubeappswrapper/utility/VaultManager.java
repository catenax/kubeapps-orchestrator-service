package com.poc.kubeappswrapper.utility;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.model.VaultSecreteRequest;
import com.poc.kubeappswrapper.proxy.vault.VaultAppManageProxy;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class VaultManager {

	private final VaultAppManageProxy vaultManagerProxy;
	private final CertificateManager certificateManager;

	@Value("${vault.url}")
	private String valutURL;
	
	@Value("${vault.token}")
	private String vaulttoken;
	
	@Value("${vault.timeout}")
	private String vaulttimeout;

	@SneakyThrows
	public Map<String, String> uploadKeyandValues(String clientId, String tenantName) {

		Map<String, String> tenantVaultSecret = new HashMap<>();

		tenantVaultSecret.put("daps-cert", certificateManager.readCertificate(tenantName));
		tenantVaultSecret.put("certificate-private-key", certificateManager.readPublicCertificate(tenantName));
		tenantVaultSecret.put("certificate-private-key-pub", certificateManager.readPublicCertificate(tenantName));
		

		VaultSecreteRequest vaultSecreteRequest = VaultSecreteRequest.builder().data(tenantVaultSecret).build();

		String valutURLwithpath = valutURL + "/v1/secret/data/" + tenantName;
		URI url = new URI(valutURLwithpath);
		vaultManagerProxy.uploadKeyandValue(url, vaultSecreteRequest);
		
		tenantVaultSecret = new HashMap<>();
		tenantVaultSecret.put("daps-cert", tenantName+"/daps-cert");
		tenantVaultSecret.put("certificate-private-key", tenantName+"/certificate-private-key");
		tenantVaultSecret.put("vaulturl", valutURL);
		tenantVaultSecret.put("vaulttoken", vaulttoken);
		tenantVaultSecret.put("vaulttimeout", vaulttimeout);
		
		return tenantVaultSecret;
	}

}
