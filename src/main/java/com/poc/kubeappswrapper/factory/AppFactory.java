package com.poc.kubeappswrapper.factory;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.constant.AppConstant;
import com.poc.kubeappswrapper.factory.builder.EDCControlPlaneBuilder;
import com.poc.kubeappswrapper.factory.builder.EDCDataPlaneBuilder;
import com.poc.kubeappswrapper.factory.builder.PostgresDBBuilder;
import com.poc.kubeappswrapper.wrapper.model.CreatePackageRequest;

@Component
public class AppFactory {

	@Autowired
	private EDCControlPlaneBuilder edcControlPlaneBuilder;

	@Autowired
	private EDCDataPlaneBuilder edcDataPlaneBuilder;

	@Autowired
	private PostgresDBBuilder postgresDBBuilder;

	public CreatePackageRequest getAppInputRequestwithrequireDetails(AppConstant app, String tenantName,
			Map<String, String> inputProperties) {

		CreatePackageRequest createPackageRequest = prepareRequestPojo(app);

		switch (app) {

		case EDC_CONTROLPLANE:
			createPackageRequest.setValues(
					edcControlPlaneBuilder.buildConfiguration(app.getAppName(), tenantName, inputProperties));
			break;
		case EDC_DATAPLANE:
			createPackageRequest
					.setValues(edcDataPlaneBuilder.buildConfiguration(app.getAppName(), tenantName, inputProperties));
			break;
		case POSTGRES_DB:
			createPackageRequest.setValues(postgresDBBuilder.buildConfiguration(app.getAppName(), tenantName, inputProperties));
			break;

		default:
			break;

		}

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

}
