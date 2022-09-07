package com.poc.kubeappswrapper.workflow.steps.dftfrontendcreation;

import com.poc.kubeappswrapper.manager.KubeAppsPackageManagement;
import com.poc.kubeappswrapper.utility.PasswordGenerator;
import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import com.poc.kubeappswrapper.workflow.steps.dftbackendcreation.DftBackendStep;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;
import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_FRONTEND;

@Component
@Scope("thread")
public class DftFrontendStep extends Task {

    @Autowired
    private StartStep startStep;

    @Autowired
    private DftBackendStep dftBackendStep;

    @Autowired
    private KubeAppsPackageManagement appManagement;

    @Getter
    private Map<String, String> configParams;

    @Override
    public void run() {
        Map<String, String> inputData = new ConcurrentHashMap<>();
        inputData.putAll(startStep.getConfigParams());

        // Get DFT backend config params
        inputData.putAll(dftBackendStep.getConfigParams());

        appManagement.createPackage(DFT_FRONTEND, startStep.getCustomerDetails().getTenantName(), inputData);

        configParams = inputData;
    }
}
