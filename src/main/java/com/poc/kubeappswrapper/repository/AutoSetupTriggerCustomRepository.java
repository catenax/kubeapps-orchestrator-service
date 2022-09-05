package com.poc.kubeappswrapper.repository;

import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;

@Repository
public class AutoSetupTriggerCustomRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public AutoSetupTriggerDetails save(AutoSetupTriggerDetails autoSetupTriggerDetails) throws Exception {
		jdbcTemplate.update(
				"insert into auto_setup_trigger_details_tbl (id, trigger_id, step, status, remark) values(?,?,?,?,?)",
				autoSetupTriggerDetails.getId(), autoSetupTriggerDetails.getTriggerIdforinsert(),
				autoSetupTriggerDetails.getStep(), autoSetupTriggerDetails.getStatus(),
				autoSetupTriggerDetails.getRemark());
		return autoSetupTriggerDetails;
	}

	public AutoSetupTriggerEntry saveTriggerUpdate(AutoSetupTriggerEntry autoSetupTriggerEntry) throws Exception {
		
		jdbcTemplate.update(
				"UPDATE auto_setup_trigger_tbl set status=?, modified_timestamp=?, remark=? where status=? and autosetup_tenant_name=? and bpn_number=?",
				"CLOSED",
				autoSetupTriggerEntry.getModifiedTimestamp(), "Force close",
				TriggerStatusEnum.MANUAL_UPDATE_PENDING.name(), autoSetupTriggerEntry.getAutosetupTenantName(), autoSetupTriggerEntry.getBpnNumber());
		
		jdbcTemplate.update(
				"UPDATE auto_setup_trigger_tbl set status=? ,autosetup_result=?, modified_timestamp=?, remark=? where trigger_id=?",
				autoSetupTriggerEntry.getStatus(), autoSetupTriggerEntry.getAutosetupResult(),
				autoSetupTriggerEntry.getModifiedTimestamp(), autoSetupTriggerEntry.getRemark(),
				autoSetupTriggerEntry.getTriggerId());

		return autoSetupTriggerEntry;
	}

}
