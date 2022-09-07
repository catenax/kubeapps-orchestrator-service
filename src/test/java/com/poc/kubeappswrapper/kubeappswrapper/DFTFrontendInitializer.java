package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.entity.AppDetails;

public class DFTFrontendInitializer {

    public static AppDetails getAppDetails() {
        var appDetails = new AppDetails();
        appDetails.setAppName("DFT_FRONTEND");
        appDetails.setExpectedInputData(
                """
                {"REACT_APP_API_URL":"dftbackendurl",
                "REACT_APP_API_KEY":"dftbackendapikey",
                "REACT_APP_KEYCLOAK_URL":"dftkeycloakurl",
                "REACT_APP_KEYCLOAK_REALM":"dftcloakrealm",
                "REACT_APP_CLIENT_ID":"dftfrontendkeycloakclientid",
                "REACT_APP_DEFAULT_COMPANY_BPN":"bpnnumber",
                "REACT_APP_FILESIZE":"268435456"}
                """
        );
        appDetails.setPackageIdentifier("orch-repo/dftfrontend");
        appDetails.setPackageVersion("1.2.0");
        appDetails.setRequiredYamlConfiguration("{\"ingresses\":[{\"enabled\": true, \"hostname\":\"${dnsName}\", \"className\": \"nginx\", \"endpoints\":[\"default\"], \"tls\":{\"enabled\":true, \"tlsSecret\": \"frontend\"}}], \"configuration\": {\"properties\": \"${yamlValues}\"}}");
        appDetails.setYamlValueFieldType("PROPERTY");
        appDetails.setContextCluster("default");
        appDetails.setContextNamespace("kubeapps");
        appDetails.setPluginName("helm.packages");
        appDetails.setPluginVersion("v1alpha1");
        return appDetails;
    }
}
