package com.poc.kubeappswrapper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poc.kubeappswrapper.service.KubeAppsOrchitestratorService;

@RestController
public class AppHandlerController {

	@Autowired
	private KubeAppsOrchitestratorService appHandlerService;

	@GetMapping("/get-all-install-packages")
	public String getAllInstallPackages() {

		return appHandlerService.getAllInstallPackages();
	}

	@PostMapping("/create-package")
	public String createPackage(String tenantName,  String bpnNumber,
			String role) {
		return appHandlerService.createPackage(tenantName, bpnNumber, role);
	}

	@PutMapping("/update-package")
	public String updatePackage(@RequestParam String tenantName, @RequestParam String bpnNumber,
			@RequestParam String role) {
		return appHandlerService.updatePackage(tenantName, bpnNumber, role);
	}

	@DeleteMapping("/delete-package")
	public String deletePackage(@RequestParam String tenantName) {
		return appHandlerService.deletePackage(tenantName);
	}
}
