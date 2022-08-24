package com.poc.kubeappswrapper.workflow.steps.vaultupload;

import com.poc.kubeappswrapper.manager.VaultManager;
import com.poc.kubeappswrapper.utility.Certutil;
import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.CertificateStep;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("thread")
public class VaultUploadStep extends Task {

    private String targetCluster = "default";
    private String targetNamespace = "kubeapps";

    @Autowired
    private CertificateStep certificateStep;

    @Autowired
    private StartStep startStep;

    @Autowired
    private VaultManager vaultManager;

    @Getter
    private Map<String, String> tenantKeyinVault;
    @Override
    public void run() {

        Map<String, String> inputConfiguration = new HashMap<>();
        inputConfiguration.put("dsnName", "localhost");
        inputConfiguration.put("targetCluster", targetCluster);
        inputConfiguration.put("targetNamespace", targetNamespace);
        inputConfiguration.put("dapsclientid", Certutil.getClientId(certificateStep.getCertificateDetails().certificate()));
        tenantKeyinVault = vaultManager.uploadKeyandValues(startStep.getCustomerDetails(), inputConfiguration);

    }
}
