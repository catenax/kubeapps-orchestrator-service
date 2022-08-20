package simplewfms;

import com.poc.kubeappswrapper.utility.LombokTool;
import com.poc.kubeappswrapper.utility.TailRec;
import com.poc.kubeappswrapper.utility.ThrowingFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

public class Workflow implements Runnable{

    final Map<String, Task<?>> tasks = new HashMap<>();
    Executor executor;

    public Workflow(Executor executor) {
        this.executor = executor;
    }


    @Override
    public void run() {
        try {
            CompletionService<Task<?>> cs = new ExecutorCompletionService<>(executor);
            prepare();
            List<Task<?>> ready = tasks.values().stream().filter(Task::isReadyToRun).toList();
            int activeThreads = 0;
            while (!ready.isEmpty() || activeThreads > 0) {
                ready.stream().peek(task -> task.status = Status.ACTIVE).forEach(task -> cs.submit(task.processes, task));
                activeThreads += ready.size();
                var finishedTask = cs.take().get();
                activeThreads--;
                finishedTask.status = Status.DONE;
                ready = finishedTask.dependedOn.stream()
                        .filter(Task::isReadyToRun)
                        .toList();
            }
        } catch (ExecutionException e) {
            throw LombokTool.sneakyThrow(e.getCause());
        } catch (InterruptedException e) {
            throw LombokTool.sneakyThrow(e);
        }
    }

    private void prepare() {
        for (Task<?> task : tasks.values()) {
            for (String name : task.dependsOnName) {
                var dependsOn = tasks.get(name);
                task.dependsOn.add(dependsOn);
                dependsOn.dependedOn.add(task);
            }
        }
    }

    public <T extends Task<T>> T registerTask(T task, String name) {
        task.self = task;
        task.workflow = this;
        task.name = name;
        tasks.put(name, task);
        return task;
    }

    public Task<?> getTask(String name) {
        return tasks.get(name);
    }

    public Workflow loadTasks(String packageName) {
        findAllClassesUsingClassLoader(packageName, new HashSet<>(), new HashSet<>()).eval().stream()
                .map(className -> className.substring(0, className.lastIndexOf('.')))
                .map((ThrowingFunction<String, Class<?>>)Class::forName)
                .filter(Task.class::isAssignableFrom)
                .map(Class::getDeclaredConstructors)
                .flatMap(Arrays::stream)
                .filter(c -> c.getParameterCount() == 0)
                .map((ThrowingFunction<Constructor<?>, Object>)Constructor::newInstance)
                .map(Task.class::cast)
                .forEach(t -> registerTask(t, t.name));
        return this;
    }

    private TailRec<Set<String>> findAllClassesUsingClassLoader(String packageName, Set<String> res, Set<String> dirs) {
        try {
                var packageDir = packageName.replaceAll("[.]", "/");
                var stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageDir);
                if (Objects.isNull(stream)) return TailRec.ret(res);
                var reader = new BufferedReader(new InputStreamReader(stream));
                var it = reader.lines().iterator();
                while (it.hasNext()) {
                    var entryName = it.next();
                    var fullPath = packageDir + "/" + entryName;
                    var url = ClassLoader.getSystemClassLoader().getResource(fullPath);
                    if (Objects.nonNull(url) && url.getProtocol().equals("file")) {
                        var entry = new File(url.toURI());
                        if (entry.isDirectory()) {
                            dirs.add(fullPath);
                        } else if (entryName.endsWith(".class")) {
                            res.add(packageName + "." + entryName);
                        }
                    }
                }
                var dirsIt = dirs.iterator();
                if (dirsIt.hasNext()) {
                    var newPackageName = dirsIt.next().replaceAll("/", ".");
                    dirsIt.remove();
                    return TailRec.sus(() -> findAllClassesUsingClassLoader(newPackageName, res, dirs));
                } else {
                    return TailRec.ret(res);
                }
        } catch (Throwable throwable) {
            throw LombokTool.sneakyThrow(throwable);
        }
    }
}
