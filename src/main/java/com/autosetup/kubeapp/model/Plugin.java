package com.autosetup.kubeapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Plugin {

	private String name;
	private String version;

}
