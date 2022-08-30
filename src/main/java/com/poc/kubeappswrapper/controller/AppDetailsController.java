package com.poc.kubeappswrapper.controller;

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

	@PostMapping("/create-update-app-details")
	public AppDetails createOrUpdateAppInfo(@RequestBody AppDetailsRequest appDetailsRequest) {
		return appDetailsService.createOrUpdateAppInfo(appDetailsRequest);
	}

	@GetMapping("/get-app-details/{appName}")
	public AppDetails getAppInfo(@PathVariable("appName") String appName) {
		return appDetailsService.getAppDetails(appName);
	}
	
	@GetMapping("/get-app-details")
	public List<AppDetails> getAllAppInfo() {
		return appDetailsService.getAppDetails();
	}

}
