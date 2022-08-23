package com.poc.kubeappswrapper.workflow.steps.dapsregisration;

import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.CertificateStep;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.CREATED;

@Component
public class DapsRegistrationStep extends Task {

    @Autowired
    private StartStep startStep;

    @Autowired
    private CertificateStep certificate;

    @Autowired
    private DapsRegServiceClient dapsRegServiceClient;

    @Override
    @SneakyThrows
    public void run() {
        var status = dapsRegServiceClient.createClient(
                certificate.getCertificateDetails().certificate(),
                startStep.getToken()
        );
        if (status != CREATED) {
            throw new HttpClientErrorException(status);
        }
    }
}
