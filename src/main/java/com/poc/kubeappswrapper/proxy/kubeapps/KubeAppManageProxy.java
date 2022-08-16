package com.poc.kubeappswrapper.proxy.kubeapps;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.poc.kubeappswrapper.kubeapp.model.CreateInstalledPackageRequest;

@FeignClient(name = "KubeAppManageProxy", url = "${kubeapp.url}", configuration = ProxyConfiguration.class)
public interface KubeAppManageProxy {

	@GetMapping(path = "/apis/core/packages/v1alpha1/installedpackages")
	String getAllInstallPackages();

	@PostMapping(path = "/apis/core/packages/v1alpha1/installedpackages")
	String createPackage(@RequestBody CreateInstalledPackageRequest createInstalledPackageRequest);

	@PutMapping(path = "/apis/core/packages/v1alpha1/installedpackages/plugin/{packageName}/{packageVersion}/c/{clusterName}/ns/{namespace}/{identifier}")
	String updatePackage(@PathVariable("packageName") String packageName,
			@PathVariable("packageVersion") String packageVersion, @PathVariable("clusterName") String clusterName,
			@PathVariable("namespace") String namespace, @PathVariable("identifier") String identifier,
			@RequestBody CreateInstalledPackageRequest createInstalledPackageRequest);

	@DeleteMapping(path = "/apis/core/packages/v1alpha1/installedpackages/plugin/{packageName}/{packageVersion}/c/{clusterName}/ns/{namespace}/{identifier}")
	String deletePackage(@PathVariable("packageName") String packageName,
			@PathVariable("packageVersion") String packageVersion, @PathVariable("clusterName") String clusterName,
			@PathVariable("namespace") String namespace, @PathVariable("identifier") String identifier,
			@RequestBody CreateInstalledPackageRequest createInstalledPackageRequest);

}
