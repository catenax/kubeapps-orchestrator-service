package com.poc.kubeappswrapper.controller;

import com.poc.kubeappswrapper.model.AutoSetupTriggerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.poc.kubeappswrapper.entity.AppDetails;
import com.poc.kubeappswrapper.model.AppDetailsRequest;
import com.poc.kubeappswrapper.service.AppDetailsService;

@RestController
public class AppDetailsController {

	@Autowired
	private AppDetailsService appDetailsService;

	@Operation(summary = "This will create/update app in kubeapps",
			description = "This will create/update app in kubeapps"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AppDetails.class)))}
	)
	@PostMapping("/create-update-app-details")
	public AppDetails createOrUpdateAppInfo(@RequestBody AppDetailsRequest appDetailsRequest) {
		return appDetailsService.createOrUpdateAppInfo(appDetailsRequest);
	}

	@Operation(summary = "This will fetch specific app details in kubeapps",
			description = "This will fetch specific app details in kubeapps"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AppDetails.class)))}
	)
	@GetMapping("/get-app-details/{appName}")
	public AppDetails getAppInfo(@PathVariable("appName") String appName) {
		return appDetailsService.getAppDetails(appName);
	}

	@Operation(summary = "This will fetch all app details in kubeapps",
			description = "This will fetch all app details in kubeapps"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppDetails.class))))}
	)
	@GetMapping("/get-app-details")
	public List<AppDetails> getAllAppInfo() {
		return appDetailsService.getAppDetails();
	}

}
