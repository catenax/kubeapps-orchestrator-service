INSERT INTO app_tbl
(app_name, expected_input_data, output_data, package_identifier, package_version, required_yaml_configuration, yaml_value_field_type, context_cluster, context_namespace, plugin_name, plugin_version)
VALUES('DFT_BACKEND', '{"server.port":"8080",
    "spring.servlet.multipart.enabled":"true",
    "spring.servlet.multipart.file-size-threshold":"2KB",
    "spring.servlet.multipart.max-file-size":"200MB",
    "spring.servlet.multipart.max-request-size":"215MB",
    "server.servlet.context-path":"/api",
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
    "dft.hostname":"dft.hostname",
    "dft.apiKeyHeader":"dft.apiKeyHeader",
    "dft.apiKey":"dft.apiKey",
    "manufacturerId":"manufacturerId"}', NULL, 'dftbackend/dftbackend', '0.1.0', '{image:{repository: @s(dft-backend),tag: @s(1.0.0),pullPolicy: @s(IfNotPresent),debug: @b(false)},configuration: {properties:$yamlValues}}', 'PROPERTY', 'default', 'kubeapps', 'helm.packages', 'v1alpha1');

INSERT INTO app_tbl
(app_name, expected_input_data, output_data, package_identifier, package_version, required_yaml_configuration, yaml_value_field_type, context_cluster, context_namespace, plugin_name, plugin_version)
VALUES('DFT_FRONTEND', '{"REACT_APP_API_URL":"dftbackendurl",
"REACT_APP_API_KEY":"dftbackendapikey",
"REACT_APP_API_KEYCLOCK_URL":"dftkeyclockurl",
"REACT_APP_API_KEYCLOCK_REALM":"dftkeyclockrealm",
"REACT_APP_API_KEYCLOCK_CLIENTID":"dftkeyclockclientid",
"REACT_APP_FILESIZE":"268435456"}', NULL, 'dftfrontend/dftfrontend', '1.0.0', '{portContainer: @s(8080), image:{repository: @s(dftfrontend),tag: @s(1.0.0),pullPolicy: @s(IfNotPresent),debug: @b(false)},configuration: {properties:$yamlValues}}
', 'PROPERTY', 'default', 'kubeapps', 'helm.packages', 'v1alpha1');

INSERT INTO app_tbl
(app_name, expected_input_data, output_data, package_identifier, package_version, required_yaml_configuration, yaml_value_field_type, context_cluster, context_namespace, plugin_name, plugin_version)
VALUES('EDC_CONTROLPLANE', '{
    "edc.receiver.http.endpoint": "http://localhost:10092/edc-backend/api/v1/public",
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
    
    "edc.transfer.dataplane.token.signer.privatekey.alias": "certificate-private-key",
    
    "edc.transfer.proxy.token.signer.privatekey.alias": "certificate-private-key",
    
    "edc.oauth.public.key.alias": "daps-cert",
    "edc.oauth.private.key.alias": "certificate-private-key",
    
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
    }', NULL, 'edcrepo/edc-controlplane', '0.0.6', '{image:{repository: @s(edc-controlplane-postgresql-hashicorp-vault),tag: @s(latest),pullPolicy: @s(IfNotPresent),debug: @b(false)},configuration: {properties:$yamlValues}}
', 'PROPERTY', 'default', 'kubeapps', 'helm.packages', 'v1alpha1');

INSERT INTO app_tbl
(app_name, expected_input_data, output_data, package_identifier, package_version, required_yaml_configuration, yaml_value_field_type, context_cluster, context_namespace, plugin_name, plugin_version)
VALUES('EDC_DATAPLANE', '{"edc.hostname":"localhost",
"edc.vault.hashicorp.url":"vaulturl",
"edc.vault.hashicorp.token":"vaulttoken",
"edc.vault.hashicorp.timeout.seconds":"vaulttimeout",
"edc.dataplane.token.validation.endpoint":"controlplanevalidationendpoint"}', NULL, 'edcrepo/edc-dataplane', '0.0.6', '{configuration:{properties:$yamlValues}}', 'PROPERTY', 'default', 'kubeapps', 'helm.packages', 'v1alpha1');

INSERT INTO app_tbl
(app_name, expected_input_data, output_data, package_identifier, package_version, required_yaml_configuration, yaml_value_field_type, context_cluster, context_namespace, plugin_name, plugin_version)
VALUES('POSTGRES_DB', '{"postgresPassword":"postgresPassword",
		"username":"username",
		"password":"password",
		"database":"database"}', NULL, 'bitnami/postgresql', '11.7.1', '{global:{postgresql:{auth:$yamlValues}}}', 'JSON', 'default', 'kubeapps', 'helm.packages', 'v1alpha1');
