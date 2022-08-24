package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.proxy.vault.VaultAppManageProxy;
import com.poc.kubeappswrapper.service.WorkflowRunner;
import com.poc.kubeappswrapper.workflow.Workflow;
import com.poc.kubeappswrapper.workflow.steps.dapsregisration.DapsRegServiceClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
public class WorkflowTest {

    @Autowired
    WorkflowRunner workflowRunner;

    @MockBean
    DapsRegServiceClient dapsRegServiceClient;

    @MockBean
    VaultAppManageProxy vaultAppManageProxy;


    @Test
    public void workflowTest() throws IOException, ExecutionException, InterruptedException {
        Mockito.when(dapsRegServiceClient.createClient(any(), any())).thenReturn(HttpStatus.CREATED);
        var customerDetails = CustomerDetails.builder()
                 .bpnNumber("BPN123456")
                 .tenantName("Test-Tenant")
                 .build();
         Workflow w = workflowRunner.runWorkflow(customerDetails, "token").completable().get();
    }
}
