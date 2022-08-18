package com.poc.kubeappswrapper.service;

import com.poc.kubeappswrapper.workflow.steps.CertificateStep;
import com.poc.kubeappswrapper.workflow.steps.InputDataStep;
import com.poc.kubeappswrapper.workflow.steps.dapsregisration.DapsRegServiceClient;
import com.poc.kubeappswrapper.workflow.steps.dapsregisration.DapsRegistrationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import simplewfms.Workflow;

import javax.servlet.http.HttpServletRequest;

@Service
@RequestScope
public class WMService {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private DapsRegServiceClient dapsRegServiceClient;
    @Autowired
    private ThreadPoolTaskExecutor executor;

    public Workflow runWorkflow(String tenantName, String bpnNumber, String role) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token = header.split(" ")[1].trim();
        var workflow = new Workflow(executor);
        new CertificateStep(workflow);
        new DapsRegistrationStep(workflow, dapsRegServiceClient);
        new InputDataStep(workflow, tenantName, bpnNumber, role, token);
        return workflow;
    }

    @Async
    public void runWorkflow(Workflow workflow) {
        workflow.run();
    }
}
