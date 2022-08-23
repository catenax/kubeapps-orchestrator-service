package com.poc.kubeappswrapper.workflow;

import java.util.Collection;
import java.util.HashSet;


public abstract class Task implements Runnable {

    Collection<Task> dependsOn = new HashSet<>();
    Collection<Task> dependedOn = new HashSet<>();
    Status status = Status.READY;

    boolean isReadyToRun() {
        return status != Status.DONE && dependsOn.stream().allMatch(t -> t.status == Status.DONE);
    }
}
