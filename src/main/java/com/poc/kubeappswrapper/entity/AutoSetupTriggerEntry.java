package com.poc.kubeappswrapper.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "auto_setup_trigger_tbl")
public class AutoSetupTriggerEntry {

	@Id
	private String triggerKey;

	private String autosetupTenantName;

	private String autosetupTriggerDetails;

	private String createdTimestamp;

	private String modifiedTimestamp;

	private String status;

	private String remark;

}
