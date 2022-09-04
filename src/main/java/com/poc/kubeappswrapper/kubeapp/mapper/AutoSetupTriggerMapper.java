package com.poc.kubeappswrapper.kubeapp.mapper;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.model.AutoSetupTriggerResponse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class AutoSetupTriggerMapper {

	@Autowired
	private CustomerDetailsMapper customerDetailsMapper;

	public abstract AutoSetupTriggerResponse fromEntity(AutoSetupTriggerEntry autoSetupTriggerEntry);

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public Map<String, String> fromJsonStr(String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (jsonStr != null && !jsonStr.isEmpty())
				return mapper.readValue(jsonStr, HashMap.class);
			else
				return Map.of();
		} catch (Exception e) {
			log.error("Error in read value of autosetup field result" + e.getMessage());
			return Map.of();
		}

	}

	public AutoSetupTriggerResponse fromEntitytoCustom(AutoSetupTriggerEntry autoSetupTriggerEntry) {

		AutoSetupTriggerResponse obj = fromEntity(autoSetupTriggerEntry);
		obj.setRequest(customerDetailsMapper.fromCustomerStr(obj.getAutosetupRequest()));
		obj.setProcessResult(fromJsonStr(obj.getAutosetupResult()));
		return obj;

	}

}
