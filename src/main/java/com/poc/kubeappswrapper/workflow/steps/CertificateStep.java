package com.poc.kubeappswrapper.workflow.steps;

import com.poc.kubeappswrapper.utility.Certutil;
import com.poc.kubeappswrapper.workflow.Task;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.poc.kubeappswrapper.utility.Certutil.generateSelfSignedCertificateSecret;

@Component
@Scope("thread")
public class CertificateStep extends Task {

    @Getter
    private Certutil.CertKeyPair certificateDetails;

    @Autowired
    private StartStep startStep;


    @Override
    @SneakyThrows
    public void run() {
        var name = String.format("O=%s, OU=%s, C=DE",
                startStep.getCustomerDetails().getTenantName(),
                startStep.getCustomerDetails().getBpnNumber());
        certificateDetails = generateSelfSignedCertificateSecret(name, null, null);
    }
}
