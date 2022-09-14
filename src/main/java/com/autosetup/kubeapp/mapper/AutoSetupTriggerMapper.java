package com.autosetup.kubeapp.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.model.AutoSetupResponse;
import com.autosetup.model.AutoSetupTriggerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class AutoSetupTriggerMapper {

	@Autowired
	private AutoSetupRequestMapper autoSetupRequestMapper;

	public abstract AutoSetupTriggerResponse fromEntity(AutoSetupTriggerEntry autoSetupTriggerEntry);
	
	public abstract AutoSetupResponse fromEntityforCustomResponse(AutoSetupTriggerEntry autoSetupTriggerEntry);

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public List<Map<String, String>> fromJsonStrToMap(String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (jsonStr != null && !jsonStr.isEmpty())
				return (List<Map<String, String>>) mapper.readValue(jsonStr, List.class);
			else
				return List.of();
		} catch (Exception e) {
			log.error("Error in read value of autosetup field result" + e.getMessage());
			return List.of();
		}

	}

	@SneakyThrows
	public String fromMaptoStr(List<Map<String, String>> Listmap) {
		try {
			if (Listmap != null && !Listmap.isEmpty())
				return new ObjectMapper().writeValueAsString(Listmap);
		} catch (Exception e) {
			log.error("Error in read value of autosetup field result" + e.getMessage());
		}
		return "{}";
	}

	public AutoSetupTriggerResponse fromEntitytoCustom(AutoSetupTriggerEntry autoSetupTriggerEntry) {

		AutoSetupTriggerResponse obj = fromEntity(autoSetupTriggerEntry);
		obj.setRequest(autoSetupRequestMapper.fromStr(obj.getAutosetupRequest()));
		obj.setProcessResult(fromJsonStrToMap(obj.getAutosetupResult()));
		return obj;

	}
	
	public AutoSetupResponse fromEntitytoAutoSetupCustom(AutoSetupTriggerEntry autoSetupTriggerEntry) {

		AutoSetupResponse obj = fromEntityforCustomResponse(autoSetupTriggerEntry);
		obj.setExecutionId(autoSetupTriggerEntry.getTriggerId());
		obj.setExecutionType(autoSetupTriggerEntry.getTriggerType());
		obj.setRequest(autoSetupRequestMapper.fromStr(autoSetupTriggerEntry.getAutosetupRequest()));
		obj.setProcessResult(fromJsonStrToMap(autoSetupTriggerEntry.getAutosetupResult()));
		return obj;

	}
	
	

}
