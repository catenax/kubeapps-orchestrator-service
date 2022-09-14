package com.autosetup.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autosetup.manager.AutoSetupTriggerManager;
import com.autosetup.model.AutoSetupRequest;
import com.autosetup.model.AutoSetupResponse;
import com.autosetup.model.DFTUpdateRequest;
import com.autosetup.service.AutoSetupOrchitestratorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "AutoSetup", description = "Auto setup controller to perform all operation")
public class AutoSetupHandlerController {

	@Autowired
	private AutoSetupOrchitestratorService appHandlerService;

	@Autowired
	private AutoSetupTriggerManager autoSetupTriggerManager;

	/// internal access
	@GetMapping("/internal")
	public String getAllInstallPackages() {
		return appHandlerService.getAllInstallPackages();
	}

	/// internal access
	// update dft packages input: keycloack details for frontend and backend,
	// digital twin details
	@Operation(summary = "Update DFT only packages", description = "This will update only DFT packages")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(implementation = UUID.class))) })
	@PutMapping("/internal/update-package/{executionId}")
	public String updateDftPackage(@PathVariable("executionId") String executionId,
			@RequestBody DFTUpdateRequest dftUpdateRequest) {
		return appHandlerService.updateDftPackage(executionId, dftUpdateRequest);
	}

	// portal access
	@Operation(summary = "Start autosetup process", description = "This API will use to start the Auto setup process")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(implementation = UUID.class))) })
	@PostMapping("/autosetup")
	public String createPackage(@Valid @RequestBody AutoSetupRequest autoSetupRequest) {
		return appHandlerService.createPackage(autoSetupRequest);
	}

	// portal access
	@Operation(summary = "Update existing autosetup packages", description = "This API will use to update the existing packages created by the Auto setup process")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = UUID.class))) })

	@PutMapping("/autosetup/{executionId}")
	public String updatePackage(@PathVariable("executionId") String executionId,
			@RequestBody @Valid AutoSetupRequest autoSetupRequest) {
		return appHandlerService.updatePackage(autoSetupRequest, executionId);
	}

	// portal access
	@Operation(summary = "Delete autosetup packages", description = "This API will use to delete the existing packages created by the Auto setup process")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Deleted", content = @Content(schema = @Schema(implementation = UUID.class))) })
	@DeleteMapping("/autosetup/{executionId}")
	public String deletePackage(@PathVariable("executionId") String executionId) {
		return appHandlerService.deletePackage(executionId);
	}

	// portal access
	@Operation(summary = "Check Auto setup execution status", description = "This API will use to check/verify Auto setup process execution status")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AutoSetupResponse.class))) })
	@GetMapping("/autosetup/{executionId}")
	public AutoSetupResponse getCheckDetails(@PathVariable("executionId") String executionId) {
		return autoSetupTriggerManager.getCheckDetails(executionId);
	}
}
