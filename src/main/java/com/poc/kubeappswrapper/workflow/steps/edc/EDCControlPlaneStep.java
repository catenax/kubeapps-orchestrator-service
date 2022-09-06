package com.poc.kubeappswrapper.workflow.steps.edc;

import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.utility.PasswordGenerator;
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

import static com.poc.kubeappswrapper.constant.AppNameConstant.EDC_CONTROLPLANE;


@Component
@Scope("thread")
public class EDCControlPlaneStep extends Task {


    @Autowired
    private StartStep startStep;

    @Autowired
    private EDCDataPlaneStep edcDataPlaneStep;

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

        String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);

        inputData.put("edcapi-key", "X-Api-Key");
        inputData.put("edcapi-key-value", generateRandomPassword);
        inputData.put("dataplanepublicurl",
                    "http://" + startStep.getCustomerDetails().getTenantName() + "edcdataplane-edc-dataplane:8185/api/public");

        String edcDb = "jdbc:postgresql://" + startStep.getCustomerDetails().getTenantName()
                    + "edcpostgresdb-postgresql:5432/postgres";
        inputData.put("edcdatabaseurl", edcDb);

        appManagement.createPackage(EDC_CONTROLPLANE, startStep.getCustomerDetails().getTenantName(), inputData);

        configParams = inputData;

    }
}
