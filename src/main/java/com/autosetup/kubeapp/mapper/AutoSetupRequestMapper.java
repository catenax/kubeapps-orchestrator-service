package com.autosetup.kubeapp.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import com.autosetup.model.AutoSetupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@Mapper(componentModel = "spring")
public abstract class AutoSetupRequestMapper {

	@SneakyThrows
	public String fromCustomer(AutoSetupRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		if (request == null)
			return "";
		return mapper.writeValueAsString(request);
	}

	@SneakyThrows
	public AutoSetupRequest fromStr(String requetsstr) {
		ObjectMapper mapper = new ObjectMapper();

		if (StringUtils.isBlank(requetsstr))
			return null;
		return mapper.readValue(requetsstr, AutoSetupRequest.class);
	}

}
