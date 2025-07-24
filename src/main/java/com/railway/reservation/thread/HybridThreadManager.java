package com.railway.reservation.thread;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import com.railway.reservation.profiling.TaskProfiler;
import com.railway.reservation.profiling.ProfilingTaskWrapper;

public class HybridThreadManager {

    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ExecutorService cpuExecutor = Executors.newWorkStealingPool();
    private final Map<String, String> taskTypeMap;
    private final MeterRegistry registry;
    private final Map<String, Counter> taskCounters = new ConcurrentHashMap<>();

    public HybridThreadManager(MeterRegistry registry) {
        this.registry = registry;
        this.taskTypeMap = loadTaskMetadata();
    }

    public void executeAuto(SmartTask task) {
        String className = task.getClass().getName();
        String type = taskTypeMap.getOrDefault(className, null);

        if (type == null && !TaskProfiler.hasType(className)) {
            System.out.println("⏱️ Profiling unknown task: " + className);
            virtualExecutor.submit(() -> new ProfilingTaskWrapper(task).runTask());
            return;
        }

        if (type == null) {
            type = TaskProfiler.getType(className);
        }

        // 🧮 Metrics counter
        final String taskTypeFinal = type;

        taskCounters.computeIfAbsent(className, name ->
            Counter.builder("task.execution.count")
                .description("Counts how many times each SmartTask runs")
                .tag("type", taskTypeFinal)
                .tag("class", name)
                .register(registry)
        ).increment();

        // 🖨️ Log routing
        String logPrefix = switch (type.toUpperCase()) {
            case "IO" -> "💾";
            case "CPU" -> "⚙️";
            case "MIXED" -> "🔀";
            default -> "❓";
        };

        System.out.printf("%s [AUTO] %s → type=%s → Routed to %s%n",
                logPrefix, className, type.toUpperCase(),
                type.equalsIgnoreCase("IO") ? "VirtualThread" : (type.equalsIgnoreCase("CPU") ? "ForkJoin (CPU)" : "Direct Execution"));

        Runnable run = task::runTask;

        switch (type.toUpperCase()) {
            case "IO" -> virtualExecutor.submit(run);
            case "CPU" -> cpuExecutor.submit(run);
            case "MIXED" -> run.run();
            default -> throw new IllegalStateException("Unknown task type: " + type);
        }
    }

    public void runCpu(Runnable task) {
        cpuExecutor.submit(task);
    }

    public void runIo(Runnable task) {
        virtualExecutor.submit(task);
    }

    public static Map<String, String> loadTaskMetadata() {
        try {
            InputStream inputStream = HybridThreadManager.class.getClassLoader().getResourceAsStream("task-metadata.json");

            if (inputStream == null) {
                throw new RuntimeException("❌ task-metadata.json not found in classpath!");
            }

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
            return mapper.readValue(inputStream, typeRef);
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to load task metadata from JSON", e);
        }
    }


    public void shutdown() {
        virtualExecutor.shutdown();
        cpuExecutor.shutdown();
    }
}
