package com.railway.reservation.thread;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

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
        String taskType = taskTypeMap.getOrDefault(className, "CPU");
        String source = "task-metadata.json (generated via ASM bytecode analysis)";

        // 🧮 Metrics counter
        taskCounters
            .computeIfAbsent(className, name ->
                Counter.builder("task.execution.count")
                    .description("Counts how many times each SmartTask runs")
                    .tag("type", taskType)
                    .tag("class", name)
                    .register(registry)
            )
            .increment();

        // 🖨️ Log routing
        String logPrefix = switch (taskType.toUpperCase()) {
            case "IO" -> "💾";
            case "CPU" -> "⚙️";
            case "MIXED" -> "🔀";
            default -> "❓";
        };

        System.out.printf("%s [AUTO] %s → type=%s → source=%s → Routed to %s%n",
                logPrefix, className, taskType.toUpperCase(), source,
                taskType.equalsIgnoreCase("IO") ? "VirtualThread" : "ForkJoin (CPU)");

        Runnable run = task::runTask;

        if ("IO".equalsIgnoreCase(taskType)) {
            virtualExecutor.submit(run);
        } else {
            cpuExecutor.submit(run);
        }
    }

    public void runCpu(Runnable task) {
        cpuExecutor.submit(task);
    }

    public void runIo(Runnable task) {
        virtualExecutor.submit(task);
    }

    private Map<String, String> loadTaskMetadata() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("task-metadata.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(in, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load task metadata", e);
        }
    }

    public void shutdown() {
        virtualExecutor.shutdown();
        cpuExecutor.shutdown();
    }
}
