package com.autosetup.model;

import java.util.List;
import java.util.Map;

import com.autosetup.entity.AutoSetupTriggerDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class AutoSetupTriggerResponse {
	
	private String triggerId;

	private String triggerType;

	private String organizationName;

	private List<AutoSetupTriggerDetails> autosetupTriggerDetails;

	private AutoSetupRequest request;
	
	private List<Map<String,String>> processResult;
	
	@JsonIgnore
	private String autosetupRequest;

	@JsonIgnore
	private String autosetupResult;

	private String createdTimestamp;

	private String modifiedTimestamp;

	private String status;

	private String remark;
	
}
