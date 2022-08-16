package com.poc.kubeappswrapper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AttributeObj {
	private String key;
	private String value;
}

