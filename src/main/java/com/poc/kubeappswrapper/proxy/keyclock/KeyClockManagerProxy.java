package com.poc.kubeappswrapper.proxy.keyclock;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KeyClockManagerProxy", url = "${keyclock.url}", configuration = KeyClockManagerConfiguration.class)
public interface  KeyClockManagerProxy {
	
	@GetMapping(path = "/auth/token")
	String readtoken(@RequestParam String input);

	@PostMapping(path = "/auth/admin/realms")
	String createRealm(@RequestParam String input);

	
	@PostMapping(path = "/auth/admin/realms")
	String createUsser(@RequestParam String input);

}
