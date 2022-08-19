package com.poc.kubeappswrapper.constant;

import lombok.Getter;

@Getter
public enum AppConstant {

	EDC_CONTROLPLANE("EDC_CONTROLPLANE", "edcrepo/edc-controlplane", "0.0.6"),

	EDC_DATAPLANE("EDC_DATAPLANE", "edcrepo/edc-dataplane", "0.0.6"),

	POSTGRES_DB("POSTGRES_DB", "bitnami/postgresql", "11.7.1"),
	
	DFT_FRONTEND("DFT_FRONTEND", "dftfrontend/dftfrontend", "1.0.0"),
	
	DFT_BACKEND("DFT_BACKEND", "dftbackend/dftbackend", "0.1.0");

	private String appName;
	private String packageIdentifier;
	private String packageVersion;

	private AppConstant(String appName, String packageIdentifier, String packageVersion) {

		this.appName = appName;
		this.packageIdentifier = packageIdentifier;
		this.packageVersion = packageVersion;

	}

}
