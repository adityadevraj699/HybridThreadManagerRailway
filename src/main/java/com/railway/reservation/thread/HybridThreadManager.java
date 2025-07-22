package com.railway.reservation.thread;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HybridThreadManager {

    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ExecutorService cpuExecutor = Executors.newWorkStealingPool();
    private final Map<String, String> taskTypeMap;

    public HybridThreadManager() {
        this.taskTypeMap = loadTaskMetadata();
    }

    public void executeAuto(SmartTask task) {
        String className = task.getClass().getName();
        String taskType = taskTypeMap.getOrDefault(className, "CPU"); // default to CPU

        if ("IO".equalsIgnoreCase(taskType)) {
            System.out.println("💾 [Static] Routed to VirtualThread: " + className);
            virtualExecutor.submit(task::runTask);
        } else {
            System.out.println("⚙️ [Static] Routed to ForkJoin (CPU): " + className);
            cpuExecutor.submit(task::runTask);
        }
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
