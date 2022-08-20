package com.poc.kubeappswrapper.workflow.steps.dapsregisration;

import com.poc.kubeappswrapper.utility.Certutil;
import org.springframework.web.client.HttpClientErrorException;
import simplewfms.Task;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.CREATED;

public class DapsRegistrationStep extends Task<DapsRegistrationStep> {

    private final Supplier<Certutil.CertKeyPair> certificateSupplier = registerExternalParameter("Certificate", "CERTIFICATE");
    private final Supplier<String> tokenSupplier = registerExternalParameter("InputData", "TOKEN");
    private final Supplier<DapsRegServiceClient> dapsClientSupplier = registerExternalParameter("InputData", "DAPS_REG_CLIENT");

    public DapsRegistrationStep() {
        super();
        name = "DAPS Registration";
    }

    @Override
    public void runThrows() throws Exception {
        var status = dapsClientSupplier.get().createClient(certificateSupplier.get().certificate(), tokenSupplier.get());
        if (status != CREATED) {
            throw new HttpClientErrorException(status);
        }
    }
}
