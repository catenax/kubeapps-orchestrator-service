package com.autosetup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autosetup.entity.AutoSetupTriggerEntry;

public interface AutoSetupTriggerEntryRepository extends JpaRepository<AutoSetupTriggerEntry, String> {

	AutoSetupTriggerEntry findAllByTriggerId(String triggerId);

	AutoSetupTriggerEntry findTop1ByOrganizationNameAndStatusIsNot(String organizationName, String status);

}
