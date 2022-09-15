package com.autosetup.model;

import java.util.List;
import java.util.Map;

import com.autosetup.constant.TriggerStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoSetupResponse {

	private String executionId;

	private String executionType;

	private AutoSetupRequest request;

	private List<Map<String, String>> processResult;

	private TriggerStatusEnum status;

	private String createdTimestamp;

	private String modifiedTimestamp;

	private String remark;

}
