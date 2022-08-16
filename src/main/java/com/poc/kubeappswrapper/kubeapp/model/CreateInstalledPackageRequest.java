package com.poc.kubeappswrapper.kubeapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateInstalledPackageRequest {

	private AvailablePackageRef availablePackageRef;
	private Context targetContext;
	private String name;
	private String values;
	private Version pkgVersionReference;
	private ReconciliationOptions reconciliationOptions;
}
