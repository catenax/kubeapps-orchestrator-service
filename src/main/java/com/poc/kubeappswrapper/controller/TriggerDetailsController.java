package com.poc.kubeappswrapper.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.poc.kubeappswrapper.manager.AutoSetupTriggerManager;
import com.poc.kubeappswrapper.model.AutoSetupTriggerResponse;

@RestController
public class TriggerDetailsController {

	@Autowired
	private AutoSetupTriggerManager autoSetupTriggerManager;

	@GetMapping("/trigger")
	public List<AutoSetupTriggerResponse> getAllTriggers() {
		return autoSetupTriggerManager.getAllTriggers();
	}

	@GetMapping("/trigger/{triggerId}")
	public AutoSetupTriggerResponse getTriggerDetails(@PathVariable("triggerId") String triggerId) {
		return autoSetupTriggerManager.getTriggerDetails(triggerId);
	}

	
	@GetMapping("/check-status/{triggerId}")
	public AutoSetupTriggerResponse getCheckDetails(@PathVariable("triggerId") String triggerId) {
		return autoSetupTriggerManager.getCheckDetails(triggerId);
	}

}
