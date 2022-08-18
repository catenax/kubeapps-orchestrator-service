package com.poc.kubeappswrapper.workflow.steps.dapsregisration;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import simplewfms.Task;
import simplewfms.Workflow;

import java.security.cert.X509Certificate;

import static org.springframework.http.HttpStatus.CREATED;

public class DapsRegistrationStep extends Task<DapsRegistrationStep> {

    private final DapsRegServiceClient dapsRegServiceClient;

    public DapsRegistrationStep(Workflow workflow, DapsRegServiceClient dapsRegServiceClient) {
        super();
        workflow.registerTask(this, "DAPS Registration");
        registerExternalParameter("Certificate", "CERTIFICATE", "certificate");
        registerExternalParameter("InputData", "TOKEN", "token");
        this.dapsRegServiceClient = dapsRegServiceClient;
    }

    @Override
    public void runThrows() throws Exception {
        var token = (String)getParameter("token");
        var certificate = (X509Certificate)getParameter("certificate");
        var status = dapsRegServiceClient.createClient(certificate, token);
        if (status != CREATED) {
            throw new HttpClientErrorException(status);
        }
    }
}
