package com.railway.reservation.profiling;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskProfiler {
    private static final File META_FILE = new File("src/main/resources/task-meta.json"); // Final destination
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, String> metaMap = new ConcurrentHashMap<>();

    static {
        try {
            if (META_FILE.exists()) {
                Map<String, String> existing = mapper.readValue(META_FILE, Map.class);
                metaMap.putAll(existing);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveToMetadata(String taskName, String type) {
        metaMap.put(taskName, type);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(META_FILE, metaMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getType(String taskName) {
        return metaMap.get(taskName);
    }

    public static boolean hasType(String taskName) {
        return metaMap.containsKey(taskName);
    }
}
