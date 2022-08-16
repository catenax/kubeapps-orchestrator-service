package com.poc.kubeappswrapper.proxy.vault;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.poc.kubeappswrapper.model.VaultSecreteRequest;

@FeignClient(name = "VaultAppManageProxy", url = "placeholder", configuration = VaultConfiguration.class)
public interface VaultAppManageProxy {

	@PostMapping
	public String uploadKeyandValue(URI url, @RequestBody VaultSecreteRequest vaultSecreteRequest);

}
