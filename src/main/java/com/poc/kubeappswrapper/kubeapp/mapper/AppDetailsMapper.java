package com.poc.kubeappswrapper.kubeapp.mapper;

import org.mapstruct.Mapper;

import com.poc.kubeappswrapper.entity.AppDetails;
import com.poc.kubeappswrapper.model.AppDetailsRequest;

@Mapper(componentModel = "spring")
public interface AppDetailsMapper {

	public AppDetails from(AppDetailsRequest appDetailsRequest);

}
