package com.poc.kubeappswrapper.manager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.CustomerDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.poc.kubeappswrapper.utility.Certutil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

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


    public HttpStatus createClient1(X509Certificate certificate, String token) throws IOException {
		
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth("");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("clientName", Certutil.getClientId(certificate));
        body.add("file", new ByteArrayResource(Certutil.getAsString(certificate).getBytes()));
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        var result = restTemplate.postForEntity(dapsRegistrationUrl, requestEntity, String.class);
        return result.getStatusCode();
        
    }

    @Retryable(value = { ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
    public Map<String, String> createClient(CustomerDetails customerDetails, Map<String, String> inputData,
                                    AutoSetupTriggerEntry triger) throws IOException {

        AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
                .id(UUID.randomUUID().toString())
                .step("DAPS")
                .triggerIdforinsert(triger.getTriggerId())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(getKeycloakToken());
        try {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("clientName", inputData.get("dapsclientid"));

        body.add("file", getTestFile(inputData.get("selfsigncertificate")));
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
            System.out.println("Path : "+dapsRegistrationUrl);
        var result = restTemplate.postForEntity(dapsRegistrationUrl, requestEntity, String.class);
        log.info("Result : "+result);
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
        System.out.println("Keycloak token : "+keycloak.tokenManager().getAccessTokenString());

        return keycloak.tokenManager().getAccessTokenString();

    }

    public static FileSystemResource getTestFile(String str) throws IOException {
        Path testFile = Files.createTempFile("test-file1", ".crt");
        System.out.println("Creating and Uploading Test File: " + testFile);
        Files.write(testFile, str.getBytes());
        return new FileSystemResource(testFile.toFile());
    }

}
