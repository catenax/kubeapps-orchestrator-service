package simplewfms;

import com.poc.kubeappswrapper.utility.ThrowingConsumer;
import com.poc.kubeappswrapper.utility.ThrowingRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Task<T extends Task<T>> implements ThrowingRunnable {

    T self;
    Workflow workflow;
    Collection<Task<?>> dependsOn = new HashSet<>();
    Collection<Task<?>> dependedOn = new HashSet<>();
    Collection<String> dependsOnName = new HashSet<>();
    protected String name;
    Status status = Status.READY;
    ThrowingRunnable processes = this;
    private final Map<String, Object> outputData = new HashMap<>();
    private final Map<String, Supplier<?>> externalData = new HashMap<>();

    boolean isReadyToRun() {
        return status != Status.DONE && dependsOn.stream().allMatch(t -> t.status == Status.DONE);
    }

    public Task<T> setOutput(String parameterName, Object value) {
        outputData.put(parameterName, value);
        return this;
    }

    public Object getParameter(String alias) {
        return externalData.get(alias).get();
    }

    public Task<T> addDependency(String dependencyName) {
        dependsOnName.add(dependencyName);
        return this;
    }

    public Task<T> registerExternalParameter(String taskName, String parameterName, String parameterAlias) {
        addDependency(taskName);
        externalData.put(parameterAlias, () -> workflow.tasks.get(taskName).outputData.get(parameterName));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <R> Supplier<R> registerExternalParameter(String taskName, String parameterName) {
        addDependency(taskName);
        return () -> (R)workflow.tasks.get(taskName).outputData.get(parameterName);
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
