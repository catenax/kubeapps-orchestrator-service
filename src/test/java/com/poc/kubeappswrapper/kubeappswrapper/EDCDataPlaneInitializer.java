package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.entity.AppDetails;

public class EDCDataPlaneInitializer {

    public static AppDetails getAppDetails() {
        var appDetails = new AppDetails();
        appDetails.setAppName("EDC_DATAPLANE");
        appDetails.setExpectedInputData(
                """
                {"edc.hostname":"localhost",
                "edc.vault.hashicorp.url":"vaulturl",
                "edc.vault.hashicorp.token":"vaulttoken",
                "edc.vault.hashicorp.timeout.seconds":"vaulttimeout",
                "edc.dataplane.token.validation.endpoint":"controlplanevalidationendpoint"}
                """
        );
        appDetails.setPackageIdentifier("edcrepo/edc-dataplane");
        appDetails.setPackageVersion("0.0.6");
        appDetails.setRequiredYamlConfiguration("{\"ingresses\":[{\"enabled\": true, \"tls\":{\"enabled\": true, \"tlsSecret\": \"edcdataplane\"}, \"hostname\": \"${dnsName}\", \"className\": \"nginx\", \"endpoints\":[\"public\"]}], \"configuration\": {\"properties\": \"${yamlValues}\"}}");
        appDetails.setYamlValueFieldType("PROPERTY");
        appDetails.setContextCluster("default");
        appDetails.setContextNamespace("kubeapps");
        appDetails.setPluginName("helm.packages");
        appDetails.setPluginVersion("v1alpha1");
        return appDetails;
    }
}
