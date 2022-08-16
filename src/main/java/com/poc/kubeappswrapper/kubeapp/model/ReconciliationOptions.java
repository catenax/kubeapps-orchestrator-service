package com.poc.kubeappswrapper.kubeapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReconciliationOptions {

	private String serviceAccountName;
	private String interval;
	private boolean suspend;
}
