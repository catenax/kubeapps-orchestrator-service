package com.autosetup.proxy.vault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

public class VaultConfiguration {

	@Bean(name = "valtrequestinterceptor")
	public VaultRequestInterceptor appRequestInterceptor() {
		return new VaultRequestInterceptor();
	}
}

@Slf4j
class VaultRequestInterceptor implements RequestInterceptor {

	@Value("${vault.token}")
	private String token;

	@Override
	public void apply(RequestTemplate template) {
		template.header("Authorization", "Bearer " + token);
		log.debug("Bearer authentication applied for vault");
	}

}