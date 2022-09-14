package com.autosetup.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;

@Repository
public class AutoSetupTriggerCustomRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public AutoSetupTriggerDetails save(AutoSetupTriggerDetails autoSetupTriggerDetails) throws Exception {
		jdbcTemplate.update(
				"insert into auto_setup_trigger_details_tbl (id, trigger_id, step, status, remark, created_date) values(?,?,?,?,?,?)",
				autoSetupTriggerDetails.getId(), autoSetupTriggerDetails.getTriggerIdforinsert(),
				autoSetupTriggerDetails.getStep(), autoSetupTriggerDetails.getStatus(),
				autoSetupTriggerDetails.getRemark(), autoSetupTriggerDetails.getCreatedDate());
		return autoSetupTriggerDetails;
	}

	public AutoSetupTriggerEntry saveTriggerUpdate(AutoSetupTriggerEntry autoSetupTriggerEntry) throws Exception {

		jdbcTemplate.update(
				"UPDATE auto_setup_trigger_tbl set status=?, modified_timestamp=?, remark=? where status=? and organization_name=?",
				"CLOSED", autoSetupTriggerEntry.getModifiedTimestamp(), "Force close",
				TriggerStatusEnum.MANUAL_UPDATE_PENDING.name(), autoSetupTriggerEntry.getOrganizationName());

		jdbcTemplate.update(
				"UPDATE auto_setup_trigger_tbl set status=? ,autosetup_result=?, modified_timestamp=?, remark=? where trigger_id=?",
				autoSetupTriggerEntry.getStatus(), autoSetupTriggerEntry.getAutosetupResult(),
				autoSetupTriggerEntry.getModifiedTimestamp(), autoSetupTriggerEntry.getRemark(),
				autoSetupTriggerEntry.getTriggerId());

		return autoSetupTriggerEntry;
	}

	public AutoSetupTriggerEntry updateTriggerAutoSetupRequest(AutoSetupTriggerEntry autoSetupTriggerEntry)
			throws Exception {

		jdbcTemplate.update(
				"UPDATE auto_setup_trigger_tbl set status=?, organization_name=?, autosetup_tenant_name=?, trigger_type=? ,autosetup_request=?, modified_timestamp=? where trigger_id=?",
				autoSetupTriggerEntry.getStatus(), autoSetupTriggerEntry.getOrganizationName(),
				autoSetupTriggerEntry.getAutosetupTenantName(), autoSetupTriggerEntry.getTriggerType(),
				autoSetupTriggerEntry.getAutosetupRequest(), autoSetupTriggerEntry.getModifiedTimestamp(),
				autoSetupTriggerEntry.getTriggerId());

		return autoSetupTriggerEntry;
	}

}
