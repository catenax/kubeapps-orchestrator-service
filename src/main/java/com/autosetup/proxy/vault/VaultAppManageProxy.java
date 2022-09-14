package com.autosetup.proxy.vault;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.autosetup.model.VaultSecreteRequest;

@FeignClient(name = "VaultAppManageProxy", url = "placeholder", configuration = VaultConfiguration.class)
public interface VaultAppManageProxy {

	@PostMapping
	public String uploadKeyandValue(URI url, @RequestBody VaultSecreteRequest vaultSecreteRequest);

}
