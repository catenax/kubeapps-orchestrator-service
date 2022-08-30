package com.poc.kubeappswrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;

public interface AutoSetupTriggerEntryRepository extends JpaRepository<AutoSetupTriggerEntry, String> {

	public AutoSetupTriggerEntry findAllByTriggerId(String triggerId);

	public AutoSetupTriggerEntry findTop1ByBpnNumberAndTriggerTypeAndStatusOrderByCreatedTimestampDesc(String bpnNumber, String triggerType, String status);
}
