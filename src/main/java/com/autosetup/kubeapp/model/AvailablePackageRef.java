package com.autosetup.kubeapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailablePackageRef {

	private Context context;
	private String identifier;
	private Plugin plugin;
}
