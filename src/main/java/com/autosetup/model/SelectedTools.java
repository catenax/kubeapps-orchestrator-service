package com.autosetup.model;

import com.autosetup.constant.ToolType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectedTools {

	private ToolType tool;

	private String label;

//	@JsonIgnore
	private String packageName;
	
}
