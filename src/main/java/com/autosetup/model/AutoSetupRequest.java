package com.autosetup.model;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class AutoSetupRequest {

	@Valid
	private Customer customer;

	private List<SelectedTools> selectedTools;

}
