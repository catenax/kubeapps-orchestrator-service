package com.autosetup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autosetup.entity.AppDetails;
import com.autosetup.exception.NoDataFoundException;
import com.autosetup.kubeapp.mapper.AppDetailsMapper;
import com.autosetup.model.AppDetailsRequest;
import com.autosetup.repository.AppRepository;

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
