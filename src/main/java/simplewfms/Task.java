package simplewfms;

import java.util.*;

public abstract class Task<T extends Task<T>> implements ThrowingRunnable{

    T self;
    Workflow workflow;
    Collection<Task<?>> dependsOn = new ArrayList<>();
    Collection<Task<?>> dependedOn = new ArrayList<>();
    Collection<String> dependsOnName = new ArrayList<>();
    String name;
    Status status = Status.READY;
    ThrowingRunnable processes = this;
    private final Map<String, Object> outputData = new HashMap<>();
    private final Map<String, AbstractMap.SimpleImmutableEntry<String, String>> externalData = new HashMap<>();

    boolean isReadyToRun() {
        return status != Status.DONE && dependsOn.stream().allMatch(t -> t.status == Status.DONE);
    }

    public Task<T> setOutput(String parameterName, Object value) {
        outputData.put(parameterName, value);
        return this;
    }

    public Object getParameter(String alias) {
        var externalParam = externalData.get(alias);
        return workflow.tasks.get(externalParam.getKey()).outputData.get(externalParam.getValue());
    }

    public Task<T> addDependency(String dependencyName) {
        dependsOnName.add(dependencyName);
        return this;
    }

    public Task<T> registerExternalParameter(String taskName, String parameterName, String parameterAlias) {
        addDependency(taskName);
        externalData.put(parameterAlias, new AbstractMap.SimpleImmutableEntry<>(taskName, parameterName));
        return this;
    }

    public Task<T> addStep(ThrowingConsumer<T> step) {
        final var old = processes;
        processes = () -> {
            old.run();
            step.accept(this.self);
        };
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Status getStatus() {
        return this.status;
    }
}
