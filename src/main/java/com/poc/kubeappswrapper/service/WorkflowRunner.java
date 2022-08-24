package com.poc.kubeappswrapper.service;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.workflow.Workflow;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class WorkflowRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Async
    public ListenableFuture<Workflow> runWorkflow(CustomerDetails customerDetails, String token) {
        try {
            var workflow = applicationContext.getBean(Workflow.class);
            workflow.reset();
            var startStep = applicationContext.getBean(StartStep.class);
            startStep.setToken(token);
            startStep.setCustomerDetails(customerDetails);
            workflow.run();
            return AsyncResult.forValue(workflow);
        } catch (Throwable throwable) {
            return AsyncResult.forExecutionException(throwable);
        }
    }
}
