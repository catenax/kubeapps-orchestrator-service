package com.poc.kubeappswrapper.workflow.steps.dftbackendcreation;

import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.utility.PasswordGenerator;
import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import com.poc.kubeappswrapper.workflow.steps.dftdbcreation.DftDbCreationStep;
import com.poc.kubeappswrapper.workflow.steps.edc.EDCControlPlaneStep;
import com.poc.kubeappswrapper.workflow.steps.edc.EDCDataPlaneStep;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;

@Component
@Scope("thread")
public class DftBackendStep extends Task {
    @Autowired
    private StartStep startStep;

    @Autowired
    private DftDbCreationStep dftDbCreationStep;

    @Autowired
    private EDCControlPlaneStep edcControlPlaneStep;

    @Autowired
    private KubeAppsPackageManagement appManagement;

    @Value("${target.cluster}")
    private String targetCluster;

    @Value("${target.namespace}")
    private String targetNamespace;

    @Value("${dns.name}")
    private String dnsName;

    @Value("${dns.name.protocol}")
    private String dnsNameURLProtocol;

    @Getter
    private Map<String, String> configParams;

    @Override
    @SneakyThrows
    public void run() {
        Map<String, String> inputData = new ConcurrentHashMap<>();
        inputData.putAll(startStep.getConfigParams());

        inputData.put("manufacturerId", startStep.getCustomerDetails().getBpnNumber());

        String backendurl = dnsNameURLProtocol + "://" + dnsName + "/dftbackend/api";
        String dftfrontend = dnsNameURLProtocol + "://" + dnsName;

        String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
        inputData.put("dftbackendurl", backendurl);
        inputData.put("dftbackendapikey", generateRandomPassword);
        inputData.put("dftbackendapiKeyHeader", "API_KEY");
        inputData.put("dftfrontendurl", dftfrontend);

        // Get DFT database config
        inputData.put("dftdatabaseurl", dftDbCreationStep.getConfigParams().get("dftdatabaseurl"));

        // Get EDC control plane config params
        inputData.putAll(edcControlPlaneStep.getConfigParams());

        appManagement.createPackage(DFT_BACKEND, startStep.getCustomerDetails().getTenantName(), inputData);

        configParams = inputData;

    }
}
