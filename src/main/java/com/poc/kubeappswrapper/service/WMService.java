package com.poc.kubeappswrapper.service;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.workflow.steps.dapsregisration.DapsRegServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import simplewfms.SimpleTask;
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

    public Workflow runWorkflow(CustomerDetails customerDetails) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        var workflow = new Workflow(executor).loadTasks("com.poc.kubeappswrapper.workflow.steps");
        workflow.registerTask(new SimpleTask(), "InputData")
                .setOutput("CustomerDetails", customerDetails)
                .setOutput("TOKEN", header.split(" ")[1].trim())
                .setOutput("DAPS_REG_CLIENT", dapsRegServiceClient);
        return workflow;
    }

    @Async
    public void runWorkflow(Workflow workflow) {
        workflow.run();
    }
}
