package com.poc.kubeappswrapper.manager;

import static com.poc.kubeappswrapper.constant.TriggerStatusEnum.FAILED;
import static com.poc.kubeappswrapper.constant.TriggerStatusEnum.INPROGRESS;
import static com.poc.kubeappswrapper.constant.TriggerStatusEnum.MANUAL_UPDATE_PENDING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.NoDataFoundException;
import com.poc.kubeappswrapper.kubeapp.mapper.AutoSetupTriggerMapper;
import com.poc.kubeappswrapper.kubeapp.mapper.CustomerDetailsMapper;
import com.poc.kubeappswrapper.model.AutoSetupTriggerResponse;
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
	private final CustomerDetailsMapper customerDetailsMapper;
	private final AutoSetupTriggerMapper autoSetupTriggerMapper;

	public AutoSetupTriggerEntry createTrigger(CustomerDetails customerDetails, AppActions action, String triggerId) {
		LocalDateTime now = LocalDateTime.now();
		AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
				.organizationName(customerDetails.getOrganizationName()).bpnNumber(customerDetails.getBpnNumber())
				.autosetupRequest(customerDetailsMapper.fromCustomer(customerDetails)).triggerId(triggerId)
				.triggerType(action.name()).createdTimestamp(now.toString()).modifiedTimestamp(now.toString())
				.status(INPROGRESS.name()).autosetupTenantName(customerDetails.getTenantName()).build();

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

	public List<AutoSetupTriggerResponse> getAllTriggers() {
		return Optional.of(autoSetupTriggerEntryRepository.findAll())
				.orElseGet(ArrayList::new)
				.stream().map((obj)-> autoSetupTriggerMapper.fromEntitytoCustom(obj))
				.toList();
	}

	public AutoSetupTriggerResponse getTriggerDetails(String triggerId) {
		return autoSetupTriggerEntryRepository.findById(triggerId)
				.map((obj)-> autoSetupTriggerMapper.fromEntitytoCustom(obj))
				.orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));
				
	}

	public AutoSetupTriggerResponse getCheckDetails(String triggerId) {

		return autoSetupTriggerEntryRepository.findById(triggerId).map(obj -> {
			AutoSetupTriggerResponse newobj= autoSetupTriggerMapper.fromEntitytoCustom(obj);
			newobj.setAutosetupTriggerDetails(null);
			newobj.setAutosetupRequest(null);
			
			if (FAILED.name().equals(obj.getStatus())) {
				newobj.setRemark("Please connect with technical team for more advice");
			}
			
			if(MANUAL_UPDATE_PENDING.name().equals(obj.getStatus())) {
				newobj.setProcessResult(Map.of());
			}
			return newobj;
		}).orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

	}

}
