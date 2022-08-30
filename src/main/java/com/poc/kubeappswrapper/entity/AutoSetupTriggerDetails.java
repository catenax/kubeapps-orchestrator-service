package com.poc.kubeappswrapper.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "auto_setup_trigger_details_tbl")
@JsonInclude(Include.NON_NULL)
public class AutoSetupTriggerDetails {

	@Id
	private String id;

	@Transient
	private String triggerIdforinsert;

	private String step;

	private String status;

	@Column(name="remark",columnDefinition="LONGTEXT")
	private String remark;

}
