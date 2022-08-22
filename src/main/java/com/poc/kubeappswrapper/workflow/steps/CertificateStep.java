package com.poc.kubeappswrapper.workflow.steps;

import com.poc.kubeappswrapper.model.CustomerDetails;
import simplewfms.Task;

import java.util.function.Supplier;

import static com.poc.kubeappswrapper.utility.Certutil.generateSelfSignedCertificateSecret;

public class CertificateStep extends Task<CertificateStep> {

    private final Supplier<CustomerDetails> customerDetailsSupplier = registerExternalParameter("InputDataStep", "CustomerDetails");

    public CertificateStep() {
        super();
        name = "Certificate";
   }

    @Override
    public void runThrows() throws Exception {
        var name = String.format("O=%s, OU=%s, C=DE",
                customerDetailsSupplier.get().getTenantName(),
                customerDetailsSupplier.get().getBpnNumber());
        setOutput(
                "CERTIFICATE",
                generateSelfSignedCertificateSecret(name, null, null)
        );
    }
}
