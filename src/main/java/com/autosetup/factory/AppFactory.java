package com.autosetup.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.autosetup.constant.AppNameConstant;
import com.autosetup.entity.AppDetails;
import com.autosetup.factory.builder.AppConfigurationBuilder;
import com.autosetup.repository.AppRepository;
import com.autosetup.wrapper.model.CreatePackageRequest;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class AppFactory {

	private final AppRepository appRepository;
	private final AppConfigurationBuilder appConfigurationBuilder;

	@SneakyThrows
	public CreatePackageRequest getAppInputRequestwithrequireDetails(AppNameConstant app,
			Map<String, String> inputProperties) {

		AppDetails appDetails = appRepository.findById(app.name()).orElseThrow(() -> new RuntimeException(
				String.format("The app %s is not supported for auto set up", app.name())));

		String targetCluster = inputProperties.get("targetCluster");
		String targetNamespace = inputProperties.get("targetNamespace");

		CreatePackageRequest createPackageRequest = prepareRequestPojo(appDetails, targetCluster, targetNamespace);
		createPackageRequest.setValues(appConfigurationBuilder.buildConfiguration(appDetails, inputProperties));
		return createPackageRequest;
	}

	private CreatePackageRequest prepareRequestPojo(AppDetails appDetails, String targetCluster,
			String targetNamespace) {
		return CreatePackageRequest.builder()
				.contextCluster(appDetails.getContextCluster())
				.contextNamespace(appDetails.getContextNamespace())
				.targetCluster(targetCluster)
				.targetNamespace(targetNamespace)
				.pluginName(appDetails.getPluginName())
				.pluginVersion(appDetails.getPluginVersion())
				.availablePackageIdentifier(appDetails.getPackageIdentifier())
				.availablePackageVersion(appDetails.getPackageVersion()).build();
	}

}
