package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.TriggerStatusEnum.FAILED;
import static com.poc.kubeappswrapper.constant.TriggerStatusEnum.INPROGRESS;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.NoDataFoundException;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.repository.AutoSetupTriggerCustomRepository;
import com.poc.kubeappswrapper.repository.AutoSetupTriggerEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class AutoSetupTriggerManager {

	private final AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;
	private final AutoSetupTriggerCustomRepository autoSetupTriggerDetailsRepository;

	public AutoSetupTriggerEntry createTrigger(CustomerDetails customerDetails, AppActions action, String triggerId) {
		LocalDateTime now = LocalDateTime.now();
		String json = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(customerDetails);
		} catch (Exception e) {

		}

		AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
				.organizationName(customerDetails.getOrganizationName())
				.bpnNumber(customerDetails.getBpnNumber())
				.autosetupRequest(json)
				.triggerId(triggerId)
				.triggerType(action.name())
				.createdTimestamp(now.toString())
				.modifiedTimestamp(now.toString())
				.status(INPROGRESS.name())
				.autosetupTenantName(customerDetails.getTenantName()).build();

		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry saveTriggerUpdate(AutoSetupTriggerEntry autoSetupTriggerEntry) {
		return autoSetupTriggerDetailsRepository.saveTriggerUpdate(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerDetails saveTriggerDetails(AutoSetupTriggerDetails autoSetupTriggerDetails) {
		return autoSetupTriggerDetailsRepository.save(autoSetupTriggerDetails);
	}

	public List<AutoSetupTriggerEntry> getAllTriggers() {
		return autoSetupTriggerEntryRepository.findAll();
	}

	public AutoSetupTriggerEntry getTriggerDetails(String triggerId) {
		return autoSetupTriggerEntryRepository.findById(triggerId)
				.orElseThrow(() -> new NoDataFoundException("No data found for "+triggerId));
	}

	public AutoSetupTriggerEntry getCheckDetails(String triggerId) {
		
		AutoSetupTriggerEntry findAllByTriggerId = autoSetupTriggerEntryRepository.findById(triggerId)
				.map(obj -> {
						obj.setAutosetupTriggerDetails(null);
						if (FAILED.name().equals(obj.getStatus())) {
							obj.setRemark("Please connect with T-systems technical team for more advice");
						}
					 return obj;
					})
				.orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

		return findAllByTriggerId;
	}

}
