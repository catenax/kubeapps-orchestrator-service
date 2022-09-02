package com.poc.kubeappswrapper.workflow.steps.edc;

import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import com.poc.kubeappswrapper.workflow.steps.postgresedc.PostgresEdcStep;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_DATAPLANE;


@Component
@Scope("thread")
public class EDCDataPlaneStep extends Task {


    @Autowired
    private StartStep startStep;

    @Autowired
    private PostgresEdcStep postgresEdcStep;

    @Autowired
    private KubeAppsPackageManagement appManagement;

    @Value("${target.cluster}")
    private String targetCluster;

    @Value("${target.namespace}")
    private String targetNamespace;

    @Value("${dns.name}")
    private String dnsName;

    @Getter
    private Map<String, String> configParams;

    @Override
    @SneakyThrows
    public void run() {
        Map<String, String> inputData = new ConcurrentHashMap<>();
        inputData.put("dnsName", dnsName);
        inputData.put("targetCluster", targetCluster);
        inputData.put("targetNamespace", targetNamespace);

        appManagement.createPackage(EDC_DATAPLANE, startStep.getCustomerDetails().getTenantName(), inputData);

        configParams = inputData;

    }
}
