package com.poc.kubeappswrapper.workflow.steps.dapsregisration;

import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.CertificateStep;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@Component
@Scope("thread")
public class DapsRegistrationStep extends Task {

    @Value("${daps.url}")
    private String dapsurl;

    @Value("${daps.jskurl}")
    private String dapsjsksurl;

    @Autowired
    private StartStep startStep;

    @Autowired
    private CertificateStep certificate;

    @Autowired
    private DapsRegServiceClient dapsRegServiceClient;

    @Getter
    private Map<String, String> configParams;

    @Override
    @SneakyThrows
    public void run() {
        var status = dapsRegServiceClient.createClient(
                certificate.getCertificateDetails().certificate(),
                startStep.getToken()
        );
        configParams = Map.of(
                "dapsurl", dapsurl,
                "dapsjsksurl", dapsjsksurl
        );
        if (status != CREATED) {
            throw new HttpClientErrorException(status);
        }
    }
}
