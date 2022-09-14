package com.autosetup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autosetup.entity.AppDetails;

public interface AppRepository extends JpaRepository<AppDetails, String> {

}
