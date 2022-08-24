package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.service.WorkflowRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class WorkflowTest {

    @Autowired
    WorkflowRunner workflowRunner;

    @Autowired
    MockHttpServletRequest request;

    @Test
    public void workflowTest() {
         var customerDetails = CustomerDetails.builder()
                 .bpnNumber("BPN123456")
                 .tenantName("Test-Tenant")
                 .build();
         workflowRunner.runWorkflow(customerDetails);
    }
}
