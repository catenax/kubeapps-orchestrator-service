package com.poc.kubeappswrapper.workflow.steps.vaultupload;

import com.poc.kubeappswrapper.manager.VaultManager;
import com.poc.kubeappswrapper.model.VaultSecreteRequest;
import com.poc.kubeappswrapper.proxy.vault.VaultAppManageProxy;
import com.poc.kubeappswrapper.utility.Certutil;
import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.CertificateStep;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("thread")
public class VaultUploadStep extends Task {

    @Value("${vault.url}")
    private String valutURL;

    @Value("${vault.token}")
    private String vaulttoken;
    @Value("${vault.timeout}")
    private String vaulttimeout;

    private String targetCluster = "default";
    private String targetNamespace = "kubeapps";

    @Autowired
    private CertificateStep certificateStep;

    @Autowired
    private StartStep startStep;

    @Autowired
    VaultAppManageProxy vaultManagerProxy;

    @Getter
    private Map<String, String> configParams;
    @Override
    @SneakyThrows
    public void run() {

        String tenantName = startStep.getCustomerDetails().getTenantName();

        uploadSecrete(tenantName, "daps-cert",
                Map.of("content", Certutil.getAsString(certificateStep.getCertificateDetails().certificate()))
        );

        uploadSecrete(tenantName, "certificate-private-key",
                Map.of("content", Certutil.getAsString(certificateStep.getCertificateDetails().keyPair().getPrivate()))
        );
        uploadSecrete(tenantName, "certificate-private-key-pub",
                Map.of("content", Certutil.getAsString(certificateStep.getCertificateDetails().keyPair().getPrivate()))
        );

        configParams = Map.of(
                "daps-cert", tenantName + "daps-cert",
                "certificate-private-key", tenantName + "certificate-private-key",
                "vaulturl", valutURL,
                "vaulttoken", vaulttoken,
                "vaulttimeout", vaulttimeout);;
    }

    private void uploadSecrete(String tenantName, String secretePath, Map<String, String> tenantVaultSecret)
            throws URISyntaxException {

        String valutURLwithpath = valutURL + "/v1/secret/data/" + tenantName + "" + secretePath;
        VaultSecreteRequest vaultSecreteRequest = VaultSecreteRequest.builder().data(tenantVaultSecret).build();
        URI url = new URI(valutURLwithpath);
        vaultManagerProxy.uploadKeyandValue(url, vaultSecreteRequest);
    }
}
