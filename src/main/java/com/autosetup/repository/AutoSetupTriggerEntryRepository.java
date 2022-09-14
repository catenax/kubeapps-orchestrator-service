package com.autosetup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autosetup.entity.AutoSetupTriggerEntry;

public interface AutoSetupTriggerEntryRepository extends JpaRepository<AutoSetupTriggerEntry, String> {

	public AutoSetupTriggerEntry findAllByTriggerId(String triggerId);

	public AutoSetupTriggerEntry findTop1ByOrganizationNameAndStatusIsNot(String organizationName, String status);

}
