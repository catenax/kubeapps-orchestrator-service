/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

CREATE TABLE `app_tbl` (
  `app_name` varchar(255) NOT NULL,
  `context_cluster` varchar(255) DEFAULT NULL,
  `context_namespace` varchar(255) DEFAULT NULL,
  `expected_input_data` longtext,
  `output_data` longtext,
  `package_identifier` varchar(255) DEFAULT NULL,
  `package_version` varchar(255) DEFAULT NULL,
  `plugin_name` varchar(255) DEFAULT NULL,
  `plugin_version` varchar(255) DEFAULT NULL,
  `required_yaml_configuration` longtext,
  `yaml_value_field_type` longtext,
  PRIMARY KEY (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('DFT_BACKEND', 'default', 'kubeapps', '{"server.port":"8080",
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
"edc.hostname":"internalcontrolplaneservicedata",
"edc.apiKeyHeader":"edcApiKey",
"edc.apiKey":"edcApiKeyValue",

"dft.hostname":"dftBackEndUrl",
"dft.apiKeyHeader":"dftBackEndApiKeyHeader",
"dft.apiKey":"dftBackEndApiKey",
"manufacturerId":"manufacturerId",

"edc.consumer.hostname":"internalcontrolplaneservice",
"edc.consumer.apikeyheader":"edcapi-key",
"edc.consumer.apikey":"edcapi-key-value",
"edc.consumer.datauri":"/api/v1/ids/data",

"keycloak.realm":"dftcloakrealm",
"keycloak.auth-server-url":"dftkeycloakurl",
"keycloak.ssl-required":"external",
"keycloak.resource":"dftbackendkeycloakclientid",
"keycloak.use-resource-role-mappings":"true",
"keycloak.bearer-only":"true"
}', NULL, 'orch-repo/dftbackend', '1.2.1', 'helm.packages', 'v1alpha1', '{"ingresses":[{"enabled": true, "hostname":"$\{dnsName\}", "className": "nginx", "endpoints":["default"], "tls":{"enabled":true, "tlsSecret": "backendsecret"}}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('DFT_FRONTEND', 'default', 'kubeapps', '{"REACT_APP_API_URL":"dftBackEndUrl",
"REACT_APP_KEYCLOAK_URL":"dftkeycloakurl",
"REACT_APP_KEYCLOAK_REALM":"dftcloakrealm",
"REACT_APP_CLIENT_ID":"dftfrontendkeycloakclientid",
"REACT_APP_DEFAULT_COMPANY_BPN":"bpnNumber",
"REACT_APP_FILESIZE":"268435456"}', NULL, 'orch-repo/dftfrontend', '1.2.4', 'helm.packages', 'v1alpha1', '{"ingresses":[{"enabled": true, "hostname":"$\{dnsName\}", "className": "nginx", "endpoints":["default"], "tls":{"enabled":true, "tlsSecret": "frontend"}}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('EDC_CONTROLPLANE', 'default', 'kubeapps', '{"edc.receiver.http.endpoint": "http://localhost:10092/edc-backend/api/v1/public",
"edc.ids.title": "Eclipse Dataspace Connector",
"edc.ids.description": "Eclipse Dataspace Connector",
"edc.ids.id": "urn:connector:edc",
"edc.ids.security.profile": "base",
"edc.ids.endpoint": "http://localhost:8282/api/v1/ids",
"edc.ids.maintainer": "http://localhost",
"edc.ids.curator": "http://localhost",
"edc.ids.catalog.id": "urn:catalog:default",
"ids.webhook.address": "http://localhost:8282",

"edc.api.control.auth.apikey.key": "edcApiKey",
"edc.api.control.auth.apikey.value": "edcApiKeyValue",
"edc.api.auth.key": "edcApiKeyValue",
"edc.hostname": "localhost",

"edc.oauth.provider.audience": "idsc:IDS_CONNECTORS_ALL",
"edc.ids.endpoint.audience": "controlPlaneIdsEndpoint",
"edc.oauth.token.url": "dapstokenurl",
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
"edc.data.encryption.keys.alias":"encryptionkeys",

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
"edc.transfer.proxy.endpoint":"dataPlanePublicUrl"
}', NULL, 'edcrepo/edc-controlplane', '0.1.1', 'helm.packages', 'v1alpha1', '{"ingresses":[{"enabled": true,  "tls":{"enabled": true, "tlsSecret": "edccontrolplane"}, "hostname": "$\{dnsName\}", "className": "nginx", "endpoints":["ids", "data", "control", "default"]}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('EDC_DATAPLANE', 'default', 'kubeapps', '{"edc.hostname":"localhost",
"edc.vault.hashicorp.url":"vaulturl",
"edc.vault.hashicorp.token":"vaulttoken",
"edc.vault.hashicorp.timeout.seconds":"vaulttimeout",
"edc.dataplane.token.validation.endpoint":"controlPlaneValidationEndpoint"}', NULL, 'edcrepo/edc-dataplane', '0.1.1', 'helm.packages', 'v1alpha1', '{"ingresses":[{"enabled": true, "tls":{"enabled": true, "tlsSecret": "edcdataplane"}, "hostname": "$\{dnsName\}", "className": "nginx", "endpoints":["public"]}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('POSTGRES_DB', 'default', 'kubeapps', '{"postgresPassword":"postgresPassword",
		"username":"username",
		"password":"password",
		"database":"database"}', NULL, 'bitnami/postgresql', '11.8.1', 'helm.packages', 'v1alpha1', '{"global": {"postgresql" : {"auth" :$\{yamlValues\}}}}', 'JSON');
