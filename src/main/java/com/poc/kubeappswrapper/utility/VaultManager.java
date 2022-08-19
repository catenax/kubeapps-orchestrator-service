package com.poc.kubeappswrapper.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.model.VaultSecreteRequest;
import com.poc.kubeappswrapper.proxy.vault.VaultAppManageProxy;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
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
		tenantVaultSecret.put( "content", certificateManager.readCertificate(tenantName));
		uploadSecrete(tenantName, "daps-cert",tenantVaultSecret);
		
		tenantVaultSecret = new HashMap<>();
		tenantVaultSecret.put( "content", certificateManager.readPublicCertificate(tenantName));
		uploadSecrete(tenantName, "certificate-private-key", tenantVaultSecret);
		
		
		tenantVaultSecret = new HashMap<>();
		tenantVaultSecret.put( "content", certificateManager.readPublicCertificate(tenantName));
		uploadSecrete(tenantName, "certificate-private-key-pub", tenantVaultSecret);

		Map<String, String> outputVaultSecret = new HashMap<>();
		outputVaultSecret.put("daps-cert", tenantName + "/daps-cert");
		outputVaultSecret.put("certificate-private-key", tenantName + "/certificate-private-key");
		outputVaultSecret.put("vaulturl", valutURL);
		outputVaultSecret.put("vaulttoken", vaulttoken);
		outputVaultSecret.put("vaulttimeout", vaulttimeout);

		return outputVaultSecret;
	}

	
	public void uploadSecrete(String tenantName, String secretePath, Map<String, String> tenantVaultSecret)
			throws URISyntaxException {

		String valutURLwithpath = valutURL + "/v1/secret/data/" + tenantName + "/" + secretePath;
		VaultSecreteRequest vaultSecreteRequest = VaultSecreteRequest.builder().data(tenantVaultSecret).build();
		URI url = new URI(valutURLwithpath);
		vaultManagerProxy.uploadKeyandValue(url, vaultSecreteRequest);

	}

}
