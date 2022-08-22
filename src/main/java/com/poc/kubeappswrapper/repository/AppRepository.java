package com.poc.kubeappswrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poc.kubeappswrapper.entity.AppDetails;

public interface AppRepository extends JpaRepository<AppDetails, String> {

}
