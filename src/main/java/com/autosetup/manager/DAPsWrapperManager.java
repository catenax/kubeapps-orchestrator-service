package com.autosetup.manager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.ServiceException;
import com.autosetup.model.Customer;
import com.autosetup.model.SelectedTools;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DAPsWrapperManager implements Serializable{

	@Value("${dapswrapper.service.url}")
    private String dapsRegistrationUrl;
    @Value("${dapswrapper.url}")
    private String dapsurl;
    @Value("${dapswrapper.jskurl}")
    private String dapsjsksurl;
    @Value("${dapswrapper.keycloak.auth-server-url}")
    private String serverUrl ;
    @Value("${dapswrapper.keycloak.realm}")
    private String realm ;
    @Value("${dapswrapper.keycloak.resource}")
    private String client ;
    @Value("${dapswrapper.keycloak.username}")
    private String username ;
    @Value("${dapswrapper.keycloak.password}")
    private String password ;
    private final AutoSetupTriggerManager autoSetupTriggerManager;

    public DAPsWrapperManager(AutoSetupTriggerManager autoSetupTriggerManager) {
        this.autoSetupTriggerManager = autoSetupTriggerManager;
    }

    @Retryable(value = { ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
    public Map<String, String> createClient(Customer customerDetails,  SelectedTools tool, Map<String, String> inputData,
                                            AutoSetupTriggerEntry triger) {

        AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
                .id(UUID.randomUUID().toString())
                .step("DAPS")
                .triggerIdforinsert(triger.getTriggerId())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(getKeycloakToken());
        try {
        Path file = getTestFile(inputData.get("selfsigncertificate"));
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        String targetNamespace = inputData.get("targetNamespace");
        body.add("clientName", targetNamespace+"-"+tool.getPackageName());
        body.add("file", new FileSystemResource(file.toFile()));
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        var result = restTemplate.postForEntity(dapsRegistrationUrl, requestEntity, String.class);
        inputData.put("dapsurl", dapsurl);
        inputData.put("dapsjsksurl", dapsjsksurl);
        } catch (Exception ex) {
            log.error("DAPsWrapperManager failed retry attempt: : {}",
            RetrySynchronizationManager.getContext().getRetryCount() + 1);
            autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
            autoSetupTriggerDetails.setRemark(ex.getMessage());
            throw new ServiceException("DAPsWrapperManager Oops! We have an exception - " + ex.getMessage());
        } finally {
            autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
        }

        return inputData;

    }

    public String getKeycloakToken() {

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .grantType(OAuth2Constants.PASSWORD)
                .realm(realm)
                .clientId(client)
                .username(username)
                .password(password)
                .resteasyClient(
                        new ResteasyClientBuilder()
                                .connectionPoolSize(10).build()
                ).build();

        keycloak.tokenManager().getAccessToken();
        return keycloak.tokenManager().getAccessTokenString();

    }

    public static Path getTestFile(String str) throws IOException {
        Path testFile = Files.createTempFile("test-file1", ".crt");
        Files.write(testFile, str.getBytes());
        return testFile;
    }

}
