package com.poc.kubeappswrapper.constant;

import lombok.Getter;

@Getter
public enum AppConstant {

	// IDS default use policy start
	EDC_CONTROLPLANE("EDC_CONTROLPLANE", "edcrepo/edc-controlplane", "0.0.6"),

	EDC_DATAPLANE("EDC_DATAPLANE", "edcrepo/edc-dataplane", "0.0.6"),

	POSTGRES_DB("POSTGRES_DB", "bitnami/postgresql", "11.7.1");

	private String appName;
	private String packageIdentifier;
	private String packageVersion;

	private AppConstant(String appName, String packageIdentifier, String packageVersion) {

		this.appName = appName;
		this.packageIdentifier = packageIdentifier;
		this.packageVersion = packageVersion;

	}

}
