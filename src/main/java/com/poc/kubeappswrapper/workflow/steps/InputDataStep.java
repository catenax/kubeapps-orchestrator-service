package com.poc.kubeappswrapper.workflow.steps;

import simplewfms.Task;
import simplewfms.Workflow;

public class InputDataStep extends Task<InputDataStep> {

    public InputDataStep(Workflow workflow, String tenantName, String bpnNumber, String role, String token) {
        super();
        workflow.registerTask(this, "InputData");
        setOutput("TENANT_NAME", tenantName);
        setOutput("BPN_NUMBER", bpnNumber);
        setOutput("ROLE", role);
        setOutput("TOKEN", token);
    }
    @Override
    public void runThrows() {}
}
