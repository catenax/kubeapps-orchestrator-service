package com.poc.kubeappswrapper.workflow.steps;

import com.poc.kubeappswrapper.manager.EmailManager;
import com.poc.kubeappswrapper.workflow.Task;
import com.poc.kubeappswrapper.workflow.steps.dftbackendcreation.DftBackendStep;
import com.poc.kubeappswrapper.workflow.steps.dftfrontendcreation.DftFrontendStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("thread")
public class DftEmailStep extends Task {
    @Autowired
    private DftBackendStep dftBackendStep;

    @Autowired
    private DftFrontendStep dftFrontendStep;
    @Autowired
    private StartStep startStep;

    @Autowired
    private EmailManager emailManager;

    @Value("${target.cluster}")
    private String targetCluster;

    @Value("${target.namespace}")
    private String targetNamespace;

    @Value("${dns.name}")
    private String dnsOriginalName;

    @Value("${dns.name.protocol}")
    private String dnsNameURLProtocol;

    @Value("${portal.email.address}")
    private String portalEmail;


    @Override
    public void run() {

        var dftBackendParams = dftBackendStep.getConfigParams();
        var customerDetails = startStep.getCustomerDetails();
        Map<String, Object> emailContent = new HashMap<>();

        emailContent.put("helloto", "Team");
        emailContent.put("orgname", customerDetails.getOrganizationName());
        emailContent.put("dftfrontendurl", dftFrontendStep.getConfigParams().get("dftfrontendurl"));
        emailContent.put("dftbackendurl", dftBackendParams.get("dftbackendurl"));
        emailContent.put("toemail", portalEmail);
        emailManager.sendEmail(emailContent, "DFT Application Deployed Successfully", "success.html");
    }
}
