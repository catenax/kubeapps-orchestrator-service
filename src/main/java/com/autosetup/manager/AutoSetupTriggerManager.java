package com.autosetup.manager;

import static com.autosetup.constant.TriggerStatusEnum.FAILED;
import static com.autosetup.constant.TriggerStatusEnum.INPROGRESS;
import static com.autosetup.constant.TriggerStatusEnum.MANUAL_UPDATE_PENDING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.autosetup.constant.AppActions;
import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.NoDataFoundException;
import com.autosetup.kubeapp.mapper.AutoSetupRequestMapper;
import com.autosetup.kubeapp.mapper.AutoSetupTriggerMapper;
import com.autosetup.model.AutoSetupRequest;
import com.autosetup.model.AutoSetupResponse;
import com.autosetup.model.AutoSetupTriggerResponse;
import com.autosetup.repository.AutoSetupTriggerCustomRepository;
import com.autosetup.repository.AutoSetupTriggerEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class AutoSetupTriggerManager {

	private final AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;
	private final AutoSetupTriggerCustomRepository autoSetupTriggerDetailsRepository;
	private final AutoSetupRequestMapper customerDetailsMapper;
	private final AutoSetupTriggerMapper autoSetupTriggerMapper;

	public AutoSetupTriggerEntry createTrigger(AutoSetupRequest autoSetupRequest, AppActions action, String triggerId,
			String tenantNamespace) {
		LocalDateTime now = LocalDateTime.now();
		AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
				.organizationName(autoSetupRequest.getCustomer().getOrganizationName())
				.autosetupRequest(customerDetailsMapper.fromCustomer(autoSetupRequest)).triggerId(triggerId)
				.triggerType(action.name()).createdTimestamp(now.toString()).modifiedTimestamp(now.toString())
				.status(INPROGRESS.name()).autosetupTenantName(tenantNamespace).build();

		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry saveTriggerUpdate(AutoSetupTriggerEntry autoSetupTriggerEntry) {
		return autoSetupTriggerDetailsRepository.saveTriggerUpdate(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry updateTriggerAutoSetupRequest(AutoSetupRequest autoSetupRequest,
			AutoSetupTriggerEntry autoSetupTriggerEntry, AppActions action) {

		autoSetupTriggerEntry.setTriggerType(action.name());
		autoSetupTriggerEntry.setOrganizationName(autoSetupRequest.getCustomer().getOrganizationName());
		autoSetupTriggerEntry.setAutosetupRequest(customerDetailsMapper.fromCustomer(autoSetupRequest));
		autoSetupTriggerEntry.setStatus(INPROGRESS.name());

		return autoSetupTriggerDetailsRepository.updateTriggerAutoSetupRequest(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry updateTriggerAutoSetupAsInProgress(AutoSetupTriggerEntry autoSetupTriggerEntry,
			AppActions action) {

		autoSetupTriggerEntry.setTriggerType(action.name());
		autoSetupTriggerEntry.setStatus(INPROGRESS.name());

		return autoSetupTriggerDetailsRepository.updateTriggerAutoSetupRequest(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerDetails saveTriggerDetails(AutoSetupTriggerDetails autoSetupTriggerDetails) {
		autoSetupTriggerDetails.setCreatedDate(LocalDateTime.now());
		return autoSetupTriggerDetailsRepository.save(autoSetupTriggerDetails);
	}

	public List<AutoSetupTriggerResponse> getAllTriggers() {
		return Optional.of(autoSetupTriggerEntryRepository.findAll()).orElseGet(ArrayList::new).stream()
				.map((obj) -> autoSetupTriggerMapper.fromEntitytoCustom(obj)).toList();
	}

	public AutoSetupTriggerResponse getTriggerDetails(String triggerId) {
		return autoSetupTriggerEntryRepository.findById(triggerId)
				.map((obj) -> autoSetupTriggerMapper.fromEntitytoCustom(obj))
				.orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

	}

	public AutoSetupResponse getCheckDetails(String triggerId) {

		return autoSetupTriggerEntryRepository.findById(triggerId).map(obj -> {
			AutoSetupResponse newobj = autoSetupTriggerMapper.fromEntitytoAutoSetupCustom(obj);

			newobj.setRemark(null);
			if (FAILED.name().equals(obj.getStatus())) {
				newobj.setRemark("Please connect with technical team for more advice");
			}

			if (MANUAL_UPDATE_PENDING.name().equals(obj.getStatus())) {
				newobj.setProcessResult(List.of());
			}
			return newobj;

		}).orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

	}

	public AutoSetupTriggerEntry isAutoSetupAvailableforOrgnizationName(String organizationName) {
		return autoSetupTriggerEntryRepository.findTop1ByOrganizationNameAndStatusIsNot(organizationName,
				TriggerStatusEnum.FAILED.name());
	}

}
