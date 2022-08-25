package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.entity.AppDetails;

public class PostgresInitializer {

    public static AppDetails  getAppDetails() {
        var appDetails = new AppDetails();
        appDetails.setAppName("POSTGRES_DB");
        appDetails.setExpectedInputData(
                """
                {"postgresPassword":"postgresPassword",
                "username":"username",
                "password":"password",
                "database":"database"}
                """
        );
        appDetails.setPackageIdentifier("bitnami/postgresql");
        appDetails.setPackageVersion("11.7.1");
        appDetails.setRequiredYamlConfiguration("{global:{postgresql:{auth:$yamlValues}}}");
        appDetails.setYamlValueFieldType("JSON");
        appDetails.setContextCluster("default");
        appDetails.setContextNamespace("kubeapps");
        appDetails.setPluginName("helm.packages");
        appDetails.setPluginVersion("v1alpha1");
        return appDetails;
    }
}
