package com.poc.kubeappswrapper.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
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

	@Operation(summary = "Trigger autosetup packages creation",
			description = "This will trigger the orchestrator package workflow creation"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UUID.class)))}
	)
	@PostMapping("/trigger-autosetup")
	public String createPackage(@RequestBody CustomerDetails customerDetails) {
		return appHandlerService.createPackage(customerDetails);
	}

	@Operation(summary = "Update autosetup existing packages",
			description = "This will update all orchestrator workflow packages"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UUID.class)))}
	)
	@PutMapping("/update-autosetup")
	public String updatePackage(@RequestBody CustomerDetails customerDetails) {
		return appHandlerService.updatePackage(customerDetails);
	}

	 //update dft packages input: keycloack details for frontend and backend, digital twin details
	 @Operation(summary = "Update DFT only packages",
			 description = "This will update only DFT packages"
	 )
	 @ApiResponses(
			 value = {
					 @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UUID.class)))}
	 )
	@PutMapping("/update-dft-package")
	public String updateDftPackage(@RequestBody DFTUpdateRequest dftUpdateRequest) {
		return appHandlerService.updateDftPackage(dftUpdateRequest);
	}

	@Operation(summary = "Delete auto-setup packages",
			description = "This will delete all orchestrator workflow packages"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UUID.class)))}
	)
	@DeleteMapping("/delete-package")
	public String deletePackage(@RequestBody CustomerDetails customerDetails) {
		return appHandlerService.deletePackage(customerDetails);
	}
}
