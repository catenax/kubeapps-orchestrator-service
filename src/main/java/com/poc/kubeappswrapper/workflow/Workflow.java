package com.poc.kubeappswrapper.workflow;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

@Component
@RequiredArgsConstructor
@Scope("thread")
public class Workflow implements Runnable{

    private final List<Task> tasks;
    private final Executor executor;

    @Override
    @SneakyThrows
    public void run() {
        CompletionService<Task> cs = new ExecutorCompletionService<>(executor);
        List<Task> ready = tasks.stream().filter(Task::isReadyToRun).toList();
        int activeThreads = 0;
        while (!ready.isEmpty() || activeThreads > 0) {
            ready.stream().peek(task -> task.status = Status.ACTIVE).forEach(task -> cs.submit(task, task));
            activeThreads += ready.size();
            var finishedTask = cs.take().get();
            activeThreads--;
            finishedTask.status = Status.DONE;
            ready = finishedTask.dependedOn.stream()
                    .filter(Task::isReadyToRun)
                    .toList();
        }
    }

    @PostConstruct
    @SneakyThrows
    private void init() {
        for (Task t: tasks) {
            for (Field f: t.getClass().getDeclaredFields()) {
                if (Task.class.isAssignableFrom(f.getType())) {
                    f.trySetAccessible();
                    var tDependsOn = (Task)f.get(t);
                    t.dependsOn.add(tDependsOn);
                    tDependsOn.dependedOn.add(t);
                }
            }
        }
    }

    public void reset() {
        tasks.forEach(task -> task.status = Status.READY);
    }

}
