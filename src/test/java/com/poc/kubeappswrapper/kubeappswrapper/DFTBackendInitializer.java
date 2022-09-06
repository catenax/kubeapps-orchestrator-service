package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.entity.AppDetails;

public class DFTBackendInitializer {

    public static AppDetails getAppDetails() {
        var appDetails = new AppDetails();
        appDetails.setAppName("EDC_DATAPLANE");
        appDetails.setExpectedInputData(
                """
                {"server.port":"8080",
                   "spring.servlet.multipart.enabled":"true",
                   "spring.servlet.multipart.file-size-threshold":"2KB",
                   "spring.servlet.multipart.max-file-size":"200MB",
                   "spring.servlet.multipart.max-request-size":"215MB",
                   "server.servlet.context-path":"/dftbackend/api",
                   "spring.flyway.baseline-on-migrate":"true",
                   "spring.flyway.locations":"classpath:/flyway",
                   "file.upload-dir":"./temp/",
                   "logging.level.org.apache.http":"info",
                   "logging.level.root":"info",
                   
                   "spring.datasource.driver-class-name":"org.postgresql.Driver",
                   "spring.jpa.open-in-view":"false",
                   "spring.datasource.url":"dftdatabaseurl",
                   "spring.datasource.username":"username",
                   "spring.datasource.password":"password",
                   
                   "digital-twins.hostname":"digital-twins.hostname",
                   "digital-twins.authentication.url":"digital-twins.authentication.url",
                   "digital-twins.authentication.clientId":"digital-twins.authentication.clientId",	
                   "digital-twins.authentication.clientSecret":"digital-twins.authentication.clientSecret",
                   
                   "edc.enabled":"true",
                   "edc.hostname":"controlplanedataendpoint",
                   "edc.apiKeyHeader":"edcapi-key",
                   "edc.apiKey":"edcapi-key-value",
                   
                   "dft.hostname":"dftbackendurl",
                   "dft.apiKeyHeader":"dftbackendapiKeyHeader",
                   "dft.apiKey":"dftbackendapikey",
                   "manufacturerId":"manufacturerId",
                   
                   "edc.consumer.hostname":"controlplaneendpoint",
                   "edc.consumer.apikeyheader":"edcapi-key",
                   "edc.consumer.apikey":"edcapi-key-value",
                   "edc.consumer.datauri":"/api/v1/ids/data",
                   
                   "keycloak.realm":"dftcloakrealm",
                   "keycloak.auth-server-url":"dftkeycloakurl",
                   "keycloak.ssl-required":"external",
                   "keycloak.resource":"dftbackendkeycloakclientid",
                   "keycloak.use-resource-role-mappings":"true",
                   "keycloak.bearer-only":"true"
                   }
                    """
        );
        appDetails.setPackageIdentifier("orch-repo/dftbackend");
        appDetails.setPackageVersion("1.1.7");
        appDetails.setRequiredYamlConfiguration("{\"ingresses\":[{\"enabled\": true, \"hostname\":\"${dnsName}\", \"className\": \"nginx\", \"endpoints\":[\"default\"], \"tls\":{\"enabled\":true, \"tlsSecret\": \"backendsecret\"}}], \"configuration\": {\"properties\": \"${yamlValues}\"}}");
        appDetails.setYamlValueFieldType("PROPERTY");
        appDetails.setContextCluster("default");
        appDetails.setContextNamespace("kubeapps");
        appDetails.setPluginName("helm.packages");
        appDetails.setPluginVersion("v1alpha1");
        return appDetails;
    }
}
