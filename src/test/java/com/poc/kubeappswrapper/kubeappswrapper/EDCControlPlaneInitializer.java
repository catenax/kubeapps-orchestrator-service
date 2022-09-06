package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.entity.AppDetails;

public class EDCControlPlaneInitializer {

    public static AppDetails getAppDetails() {
        var appDetails = new AppDetails();
        appDetails.setAppName("EDC_CONTROLPLANE");
        appDetails.setExpectedInputData(
                """
                {"edc.receiver.http.endpoint": "http://localhost:10092/edc-backend/api/v1/public",
               "edc.ids.title": "Eclipse Dataspace Connector",
               "edc.ids.description": "Eclipse Dataspace Connector",
               "edc.ids.id": "urn:connector:edc",
               "edc.ids.security.profile": "base",
               "edc.ids.endpoint": "http://localhost:8282/api/v1/ids",
               "edc.ids.maintainer": "http://localhost",
               "edc.ids.curator": "http://localhost",
               "edc.ids.catalog.id": "urn:catalog:default",
               "ids.webhook.address": "http://localhost:8282",
               
               "edc.api.control.auth.apikey.key": "edcapi-key",
               "edc.api.control.auth.apikey.value": "edcapi-key-value",
               "edc.api.auth.key": "edcapi-key-value",
               "edc.hostname": "localhost",
               
               "edc.oauth.provider.audience": "idsc:IDS_CONNECTORS_ALL",
               "edc.oauth.token.url": "dapsurl",
               "edc.oauth.client.id": "dapsclientid",
               "edc.oauth.provider.jwks.url": "dapsjsksurl",
               
               "edc.vault.hashicorp.url": "vaulturl",
               "edc.vault.hashicorp.token": "vaulttoken",
               "edc.vault.hashicorp.timeout.seconds": "vaulttimeout",
               "edc.vault.hashicorp.api.secret.path": "valuttenantpath",
               "edc.vault.hashicorp.health.check.standby.ok": false,
               
               "edc.transfer.dataplane.token.signer.privatekey.alias": "certificate-private-key",
               
               "edc.transfer.proxy.token.signer.privatekey.alias": "certificate-private-key",
               "edc.transfer.proxy.token.verifier.publickey.alias":"certificate-private-key",
               
               "edc.oauth.public.key.alias": "daps-cert",
               "edc.oauth.private.key.alias": "certificate-private-key",
               "edc.data.encryption.keys.alias":"certificate-private-key",
               
               "edc.datasource.asset.name": "asset",
               "edc.datasource.asset.url": "edcdatabaseurl",
               "edc.datasource.asset.user": "username",
               "edc.datasource.asset.password": "password",
               
               "edc.datasource.contractdefinition.name": "contractdefinition",
               "edc.datasource.contractdefinition.url": "edcdatabaseurl",
               "edc.datasource.contractdefinition.user": "username",
               "edc.datasource.contractdefinition.password": "password",
               
               "edc.datasource.contractnegotiation.name": "contractnegotiation",
               "edc.datasource.contractnegotiation.url": "edcdatabaseurl",
               "edc.datasource.contractnegotiation.user": "username",
               "edc.datasource.contractnegotiation.password": "password",
               
               "edc.datasource.policy.name": "policy",
               "edc.datasource.policy.url": "edcdatabaseurl",
               "edc.datasource.policy.user": "username",
               "edc.datasource.policy.password": "password",
               
               "edc.datasource.transferprocess.name": "transferprocess",
               "edc.datasource.transferprocess.url": "edcdatabaseurl",
               "edc.datasource.transferprocess.user": "username",
               "edc.datasource.transferprocess.password": "password",
               "edc.transfer.proxy.endpoint":"dataplanepublicurl"
               }
                """
        );
        appDetails.setPackageIdentifier("edcrepo/edc-controlplane");
        appDetails.setPackageVersion("0.0.6");
        appDetails.setRequiredYamlConfiguration("{\"ingresses\":[{\"enabled\": true,  \"tls\":{\"enabled\": true, \"tlsSecret\": \"edccontrolplane\"}, \"hostname\": \"${dnsName}\", \"className\": \"nginx\", \"endpoints\":[\"ids\", \"data\", \"control\", \"default\"]}], \"configuration\": {\"properties\": \"${yamlValues}\"}}");
        appDetails.setYamlValueFieldType("PROPERTY");
        appDetails.setContextCluster("default");
        appDetails.setContextNamespace("kubeapps");
        appDetails.setPluginName("helm.packages");
        appDetails.setPluginVersion("v1alpha1");
        return appDetails;
    }
}
