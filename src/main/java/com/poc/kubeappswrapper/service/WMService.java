package com.poc.kubeappswrapper.service;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.workflow.Workflow;
import com.poc.kubeappswrapper.workflow.steps.StartStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

@Service
@RequestScope
public class WMService {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private Workflow workflow;
    @Autowired
    private StartStep startStep;

    @Async
    public void runWorkflow(CustomerDetails customerDetails) {
        startStep.setToken(request.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1].trim());
        startStep.setCustomerDetails(customerDetails);
        workflow.reset();
        workflow.run();
    }
}
