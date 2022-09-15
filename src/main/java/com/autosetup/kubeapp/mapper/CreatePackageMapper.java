package com.autosetup.kubeapp.mapper;

import org.mapstruct.Mapper;

import com.autosetup.kubeapp.model.AvailablePackageRef;
import com.autosetup.kubeapp.model.Context;
import com.autosetup.kubeapp.model.CreateInstalledPackageRequest;
import com.autosetup.kubeapp.model.Plugin;
import com.autosetup.kubeapp.model.ReconciliationOptions;
import com.autosetup.kubeapp.model.Version;
import com.autosetup.wrapper.model.CreatePackageRequest;

@Mapper(componentModel = "spring")
public abstract class CreatePackageMapper {

	
	public CreateInstalledPackageRequest getCreatePackageRequest(CreatePackageRequest createPackageRequest,
			String appName, String packageName) {
		appName = appName.replace("_", "");
		
		Context context = Context.builder()
				.cluster(createPackageRequest.getContextCluster())
				.namespace(createPackageRequest.getContextNamespace())
				.build();
		
		Context targetContext = Context.builder()
				.cluster(createPackageRequest.getTargetCluster())
				.namespace(createPackageRequest.getTargetNamespace())
				.build();
		
		
		Plugin plugin=Plugin.builder()
				.name(createPackageRequest.getPluginName())
				.version(createPackageRequest.getPluginVersion())
				.build();
		
		AvailablePackageRef availRef = AvailablePackageRef.builder()
				.context(context)
				.identifier(createPackageRequest.getAvailablePackageIdentifier())
				.plugin(plugin)
				.build();
		
		Version pkgVersionReference = Version.builder().version(createPackageRequest.getAvailablePackageVersion()).build();
		
		ReconciliationOptions reconciliationOptions=
				ReconciliationOptions.builder()
				.interval("0")
				.serviceAccountName(packageName+"-"+appName.toLowerCase())
				.suspend(false).build();
		
		
		CreateInstalledPackageRequest createInstalledPackageRequest = 
				CreateInstalledPackageRequest.builder()
				.availablePackageRef(availRef)
				.name(packageName+"-"+appName.toLowerCase())
				.targetContext(targetContext)
				.pkgVersionReference(pkgVersionReference)
				.values(createPackageRequest.getValues())
				.reconciliationOptions(reconciliationOptions)
				.build();
		
		return createInstalledPackageRequest;
	}
	

	public CreateInstalledPackageRequest getUpdatePackageRequest(CreatePackageRequest createPackageRequest,
			String appName, String packageName) {
		appName = appName.replace("_", "");
		
		Plugin plugin=Plugin.builder()
				.name(createPackageRequest.getPluginName())
				.version(createPackageRequest.getPluginVersion())
				.build();
		
		Context context = Context.builder()
				.cluster(createPackageRequest.getContextCluster())
				.namespace(createPackageRequest.getContextNamespace())
				.build();
		
		AvailablePackageRef availRef = AvailablePackageRef.builder()
				.context(context)
				.plugin(plugin)
				.build();
		
		Version pkgVersionReference = Version.builder()
				.version(createPackageRequest.getAvailablePackageVersion())
				.build();
		
		ReconciliationOptions reconciliationOptions=
				ReconciliationOptions.builder()
				.interval("0")
				.serviceAccountName(packageName+"-"+appName.toLowerCase())
				.suspend(false).build();
		
		CreateInstalledPackageRequest createInstalledPackageRequest = 
				CreateInstalledPackageRequest.builder()
				.availablePackageRef(availRef)
				.pkgVersionReference(pkgVersionReference)
				.values(createPackageRequest.getValues())
				.reconciliationOptions(reconciliationOptions)
				.build();
		
		return createInstalledPackageRequest;
	}
	
	
}
