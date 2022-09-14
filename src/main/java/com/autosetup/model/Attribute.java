package com.autosetup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Attribute {
	
	private String key;
	
	private String value;
	
}

