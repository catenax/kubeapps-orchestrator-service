package com.autosetup.kubeapp.mapper;

import org.mapstruct.Mapper;

import com.autosetup.entity.AppDetails;
import com.autosetup.model.AppDetailsRequest;

@Mapper(componentModel = "spring")
public interface AppDetailsMapper {

	AppDetails from(AppDetailsRequest appDetailsRequest);

}
