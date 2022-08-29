package com.poc.kubeappswrapper.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.manager.AutoSetupTriggerManager;

@RestController
public class TriggerDetailsController {

	@Autowired
	private AutoSetupTriggerManager autoSetupTriggerManager;

	@GetMapping("/trigger")
	public List<AutoSetupTriggerEntry> getAllTriggers() {
		return autoSetupTriggerManager.getAllTriggers();
	}

	@GetMapping("/trigger/{triggerId}")
	public AutoSetupTriggerEntry getTriggerDetails(@PathVariable("triggerId") String triggerId) {
		return autoSetupTriggerManager.getTriggerDetails(triggerId);
	}

	
	@GetMapping("/check-status/{triggerId}")
	public AutoSetupTriggerEntry getCheckDetails(@PathVariable("triggerId") String triggerId) {
		return autoSetupTriggerManager.getCheckDetails(triggerId);
	}

}
