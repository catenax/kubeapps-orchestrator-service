package com.poc.kubeappswrapper.proxy.kubeapps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

public class ProxyConfiguration {

	@Bean(name = "kubeappsrequestinterceptor")
	public AppRequestInterceptor appRequestInterceptor() {
		return new AppRequestInterceptor();
	}
}

@Slf4j
class AppRequestInterceptor implements RequestInterceptor {

	@Value("${kubeapp.token}")
	private String token;

	@Override
	public void apply(RequestTemplate template) {
		template.header("Authorization", "Bearer " + token);
		log.debug("Bearer authentication applied for kubeapps");
	}

}