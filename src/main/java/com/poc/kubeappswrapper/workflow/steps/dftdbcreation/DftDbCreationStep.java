package com.poc.kubeappswrapper.workflow.steps.dftdbcreation;

import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.poc.kubeappswrapper.constant.AppNameConstant.POSTGRES_DB;

@Component
@Scope("thread")
public class DftDbCreationStep extends Task {

    @Autowired
    private StartStep startStep;

    @Autowired
    private KubeAppsPackageManagement appManagement;

    @Getter
    private Map<String, String> configParams;

    @Getter
    private String jdbcUrl;

    @Override
    public void run() {
        String packagefor = "dft";
        String tenantName = startStep.getCustomerDetails().getTenantName();
        String databasefor = tenantName + "_" + packagefor;
        jdbcUrl = "jdbc:postgresql://" + tenantName + packagefor + "postgresdb-postgresql:5432/" + databasefor;

        Map<String, String> inputData = new HashMap<>();
        inputData.putAll(Map.of(
                "postgresPassword", "admin@123",
                "username", "admin",
                "password", "admin@123",
                "database", databasefor
        ));
        inputData.putAll(startStep.getConfigParams());
        inputData.put("packagefor", packagefor);
        appManagement.createPackage(POSTGRES_DB, tenantName + "" + packagefor,
                inputData);
        inputData.put("dftdatabaseurl", jdbcUrl);
        configParams = inputData;
    }
}
