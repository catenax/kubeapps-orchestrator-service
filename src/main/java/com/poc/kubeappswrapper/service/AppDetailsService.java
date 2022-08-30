package com.poc.kubeappswrapper.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.entity.AppDetails;
import com.poc.kubeappswrapper.exception.NoDataFoundException;
import com.poc.kubeappswrapper.kubeapp.mapper.AppDetailsMapper;
import com.poc.kubeappswrapper.model.AppDetailsRequest;
import com.poc.kubeappswrapper.repository.AppRepository;

@Service
public class AppDetailsService {

	@Autowired
	private AppRepository appRepository;

	@Autowired
	private AppDetailsMapper appDetailsMapper;

	public AppDetails createOrUpdateAppInfo(AppDetailsRequest appDetailsRequest) {
		AppDetails appDetails = appDetailsMapper.from(appDetailsRequest);
		return appRepository.save(appDetails);
	}

	public AppDetails getAppDetails(String appName) {

		return appRepository.findById(appName)
				.orElseThrow(() -> new NoDataFoundException("No data found for " + appName));
	}

	public List<AppDetails> getAppDetails() {
		return appRepository.findAll();
	}

}
