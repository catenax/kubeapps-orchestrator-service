package com.poc.kubeappswrapper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.model.DFTUpdateRequest;
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
	public String createPackage(@RequestBody CustomerDetails customerDetails) {
		return appHandlerService.createPackage(customerDetails);
	}

	@PutMapping("/update-package")
	public String updatePackage(@RequestBody CustomerDetails customerDetails) {
		return appHandlerService.updatePackage(customerDetails);
	}

	 //update dft packages input: keycloack details for frontend and backend, digital twin details
	@PutMapping("/update-dft-package")
	public String updateDftPackage(@RequestBody DFTUpdateRequest dftUpdateRequest) {
		return appHandlerService.updateDftPackage(dftUpdateRequest);
	}

	@DeleteMapping("/delete-package")
	public String deletePackage(@RequestBody CustomerDetails customerDetails) {
		return appHandlerService.deletePackage(customerDetails);
	}
}
