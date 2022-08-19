package com.poc.kubeappswrapper.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.constant.AppConstant;
import com.poc.kubeappswrapper.factory.builder.AppServiceBuilder;
import com.poc.kubeappswrapper.factory.builder.DFTBackendBuilder;
import com.poc.kubeappswrapper.factory.builder.DFTFrontendBuilder;
import com.poc.kubeappswrapper.factory.builder.EDCControlPlaneBuilder;
import com.poc.kubeappswrapper.factory.builder.EDCDataPlaneBuilder;
import com.poc.kubeappswrapper.factory.builder.PostgresDBBuilder;
import com.poc.kubeappswrapper.wrapper.model.CreatePackageRequest;

import lombok.SneakyThrows;

@Component
public class AppFactory {

	private AppServiceBuilder appServiceBuilder;

	@SneakyThrows
	public CreatePackageRequest getAppInputRequestwithrequireDetails(AppConstant app, String tenantName,
			Map<String, String> inputProperties) {

		CreatePackageRequest createPackageRequest = prepareRequestPojo(app);
		appServiceBuilder = getServiceInstance(app);
		
		createPackageRequest
				.setValues(appServiceBuilder.buildConfiguration(app.getAppName(), tenantName, inputProperties));

		return createPackageRequest;
	}

	private CreatePackageRequest prepareRequestPojo(AppConstant app) {
		return CreatePackageRequest.builder()
				.contextCluster("default")
				.contextNamespace("kubeapps")
				.targetCluster("default")
				.targetNamespace("kubeapps")
				.pluginName("helm.packages")
				.pluginVersion("v1alpha1")
				.availablePackageIdentifier(app.getPackageIdentifier())
				.availablePackageVersion(app.getPackageVersion()).build();
	}

	private AppServiceBuilder getServiceInstance(AppConstant app) throws Exception {
		AppServiceBuilder appServiceBuilder = null;
		switch (app) {

		case EDC_CONTROLPLANE:
			appServiceBuilder = new EDCControlPlaneBuilder();
			break;
		case EDC_DATAPLANE:
			appServiceBuilder = new EDCDataPlaneBuilder();
			break;
		case POSTGRES_DB:
			appServiceBuilder = new PostgresDBBuilder();
			break;

		case DFT_BACKEND:
			appServiceBuilder = new DFTBackendBuilder();
			break;

		case DFT_FRONTEND:
			appServiceBuilder = new DFTFrontendBuilder();
			break;

		default:
			throw new Exception("Appliaction not supported for auto setup");

		}
		return appServiceBuilder;
	}

}
