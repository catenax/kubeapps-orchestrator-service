package com.poc.kubeappswrapper.service;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.workflow.Workflow;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

@Service
@RequestScope
public class WorkflowRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest request;

    @Async
    public ListenableFuture<Workflow> runWorkflow(CustomerDetails customerDetails) {
        try {
            var workflow = applicationContext.getBean(Workflow.class);
            workflow.reset();
            var startStep = applicationContext.getBean(StartStep.class);
            startStep.setToken(request.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1].trim());
            startStep.setCustomerDetails(customerDetails);
            workflow.run();
            return AsyncResult.forValue(workflow);
        } catch (Throwable throwable) {
            return AsyncResult.forExecutionException(throwable);
        }
    }
}
