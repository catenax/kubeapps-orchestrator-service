package com.poc.kubeappswrapper.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "auto_setup_trigger_tbl")
@JsonInclude(Include.NON_NULL)
public class AutoSetupTriggerEntry {

	@Id
	private String triggerId;

	private String triggerType;
	
	private String organizationName;

	@JsonIgnore
	private String autosetupTenantName;

	@OneToMany(targetEntity = AutoSetupTriggerDetails.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "trigger_id", referencedColumnName = "triggerId")
	private List<AutoSetupTriggerDetails> autosetupTriggerDetails;

	@Column(name="autosetup_request",columnDefinition="LONGTEXT")
	private String autosetupRequest;

	@Column(name="autosetup_result",columnDefinition="LONGTEXT")
	private String autosetupResult;

	@Column(name="bpn_number")
	private String bpnNumber;
	
	private String createdTimestamp;

	private String modifiedTimestamp;

	private String status;

	@Column(name="remark",columnDefinition="LONGTEXT")
	private String remark;

	public void addTriggerDetails(AutoSetupTriggerDetails autoSetupTriggerDetails) {
		if (autosetupTriggerDetails == null)
			autosetupTriggerDetails = new ArrayList<>();
		autosetupTriggerDetails.add(autoSetupTriggerDetails);
	}

}
