package com.poc.kubeappswrapper.workflow.steps;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.workflow.Task;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("thread")
public class StartStep extends Task {

    @Getter @Setter
    private CustomerDetails customerDetails;

    @Getter @Setter
    private String token;



    @Override
    public void run() {
    }
}
