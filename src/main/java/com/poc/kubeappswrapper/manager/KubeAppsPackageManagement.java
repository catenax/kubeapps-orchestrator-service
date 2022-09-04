package com.poc.kubeappswrapper.manager;

import java.util.Map;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.AppNameConstant;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.factory.AppFactory;
import com.poc.kubeappswrapper.kubeapp.mapper.CreatePackageMapper;
import com.poc.kubeappswrapper.kubeapp.model.CreateInstalledPackageRequest;
import com.poc.kubeappswrapper.proxy.kubeapps.KubeAppManageProxy;
import com.poc.kubeappswrapper.wrapper.model.CreatePackageRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KubeAppsPackageManagement {

	private final CreatePackageMapper createPackageMapper;

	private final AppFactory appFactory;

	private final KubeAppManageProxy kubeAppManageProxy;

	public String createPackage(AppNameConstant app, String tenantName, Map<String, String> inputProperties) {
		log.info(tenantName + "-" + app.name() + " package creating");

		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app,
				inputProperties);

		String createPackage = kubeAppManageProxy.createPackage(
				createPackageMapper.getCreatePackageRequest(appWithStandardInfo, app.name(), tenantName));
		log.info(tenantName + "-" + app.name() + " package created");
		return createPackage;

	}

	public String updatePackage(AppNameConstant app, String tenantName, Map<String, String> inputProperties) {
		log.info(tenantName + "-" + app.name() + " package updating");
		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app,
				inputProperties);

		CreateInstalledPackageRequest updateControlPlane = createPackageMapper
				.getUpdatePackageRequest(appWithStandardInfo, app.name(), tenantName);

		String appName = app.name().replaceAll("_", "");

		String updatePackage = kubeAppManageProxy.updatePackage(appWithStandardInfo.getPluginName(),
				appWithStandardInfo.getPluginVersion(), appWithStandardInfo.getTargetCluster(),
				appWithStandardInfo.getTargetNamespace(), tenantName + appName.toLowerCase(), updateControlPlane);
		log.info(tenantName + "-" + app.name() + " package updated");
		return updatePackage;

	}

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public void deletePackage(AppNameConstant app, String tenantName, Map<String, String> inputProperties) {

		try {
			log.info(tenantName + "-" + app.name() + " package deleting ");
			CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app,
					inputProperties);

			CreateInstalledPackageRequest updateControlPlane = createPackageMapper
					.getUpdatePackageRequest(appWithStandardInfo, app.name(), tenantName);

			String appName = app.name().replaceAll("_", "");

			kubeAppManageProxy.deletePackage(appWithStandardInfo.getPluginName(),
					appWithStandardInfo.getPluginVersion(), appWithStandardInfo.getTargetCluster(),
					appWithStandardInfo.getTargetNamespace(), tenantName + appName.toLowerCase(), updateControlPlane);
			log.info(tenantName + "-" + app.name() + " package deleted ");
		} catch (Exception e) {

			log.error("DeletePackage failed retry attempt: : {}, Error: {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1, e.getMessage());
			
			if (!e.getMessage().contains("404"))
				throw new ServiceException(
						"Error in " + tenantName + "-" + app.name() + " package delete " + e.getMessage());
		}

	}

}
