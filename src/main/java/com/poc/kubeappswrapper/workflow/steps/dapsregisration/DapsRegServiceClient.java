package com.poc.kubeappswrapper.workflow.steps.dapsregisration;

import com.poc.kubeappswrapper.utility.Certutil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.cert.X509Certificate;

@Service
public class DapsRegServiceClient {

    @Value("${daps.url}")
    private String dapsRegistrationUrl;

    public HttpStatus createClient(X509Certificate certificate, String token) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("clientName", Certutil.getClientId(certificate));
        body.add("file", new ByteArrayResource(Certutil.getAsString(certificate).getBytes()));
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        var result = restTemplate.postForEntity(dapsRegistrationUrl, requestEntity, String.class);
        return result.getStatusCode();
    }
}
