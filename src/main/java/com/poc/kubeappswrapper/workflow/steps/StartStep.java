package com.poc.kubeappswrapper.workflow.steps;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.workflow.Task;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("thread")
public class StartStep extends Task {

    @Getter @Setter
    private CustomerDetails customerDetails;

    @Getter @Setter
    private String token;

    @Getter
    private final String targetCluster = "default";
    @Getter
    private final String targetNamespace = "kubeapps";

    @Getter
    private final Map<String, String> configParams = Map.of(
		"dsnName", "localhost",
		"targetCluster", targetCluster,
		"targetNamespace", targetNamespace);

    @Override
    public void run() {
    }
}
