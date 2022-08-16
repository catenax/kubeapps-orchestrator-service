package com.poc.kubeappswrapper.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppConstant;
import com.poc.kubeappswrapper.factory.AppFactory;
import com.poc.kubeappswrapper.kubeapp.mapper.CreatePackageMapper;
import com.poc.kubeappswrapper.kubeapp.model.Context;
import com.poc.kubeappswrapper.kubeapp.model.CreateInstalledPackageRequest;
import com.poc.kubeappswrapper.kubeapp.model.Plugin;
import com.poc.kubeappswrapper.proxy.kubeapps.KubeAppManageProxy;
import com.poc.kubeappswrapper.wrapper.model.CreatePackageRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KubeAppsPackageManagement {

	private final CreatePackageMapper createPackageMapper;

	private final AppFactory appFactory;

	private final KubeAppManageProxy kubeAppManageProxy;

	public String createPackage(AppConstant app, String tenantName, Map<String, String> inputProperties) {

		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app, tenantName,
				inputProperties);

		return kubeAppManageProxy.createPackage(
				createPackageMapper.getCreatePackageRequest(appWithStandardInfo, app.getAppName(), tenantName));

	}

	public String updatePackage(AppConstant app, String tenantName, Map<String, String> inputProperties) {

		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app, tenantName,
				inputProperties);

		CreateInstalledPackageRequest updateControlPlane = createPackageMapper
				.getUpdatePackageRequest(appWithStandardInfo, app.getAppName(), tenantName);
		Plugin plugin = updateControlPlane.getAvailablePackageRef().getPlugin();
		Context context = updateControlPlane.getAvailablePackageRef().getContext();

		String appName =app.getAppName().replaceAll("_", "");
		
		return kubeAppManageProxy.updatePackage(plugin.getName(), plugin.getVersion(), context.getCluster(),
				context.getNamespace(), tenantName+appName.toLowerCase(), updateControlPlane);

	}

	public String deletePackage(AppConstant app, String tenantName, Map<String, String> inputProperties) {

		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app, tenantName,
				inputProperties);

		CreateInstalledPackageRequest updateControlPlane = createPackageMapper
				.getUpdatePackageRequest(appWithStandardInfo, app.getAppName(), tenantName);
		Plugin plugin = updateControlPlane.getAvailablePackageRef().getPlugin();
		Context context = updateControlPlane.getAvailablePackageRef().getContext();
		String appName =app.getAppName().replaceAll("_", "");
		return kubeAppManageProxy.deletePackage(plugin.getName(), plugin.getVersion(), context.getCluster(),
				context.getNamespace(), tenantName+appName.toLowerCase(), updateControlPlane);

	}

}
