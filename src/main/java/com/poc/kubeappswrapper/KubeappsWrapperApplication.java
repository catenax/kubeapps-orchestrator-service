package com.poc.kubeappswrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class KubeappsWrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubeappsWrapperApplication.class, args);
	}

}
