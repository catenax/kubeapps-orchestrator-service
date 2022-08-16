package com.poc.kubeappswrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class KubeappsWrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubeappsWrapperApplication.class, args);
	}

}
