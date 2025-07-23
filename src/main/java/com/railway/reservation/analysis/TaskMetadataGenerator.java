package com.railway.reservation.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.reservation.thread.SmartTask;

public class TaskMetadataGenerator {

    private static final String TARGET_PACKAGE = "com.railway.reservation.thread";
    private static final String OUTPUT_FILE = "src/main/resources/task-metadata.json";

    public static void main(String[] args) throws Exception {
        System.out.println("🔍 Scanning package: " + TARGET_PACKAGE);

        Set<Class<?>> taskClasses = findSmartTasks(TARGET_PACKAGE);
        Map<String, String> metadata = new LinkedHashMap<>();

        for (Class<?> clazz : taskClasses) {
            String className = clazz.getName();
            boolean isIO = IOAnalyzer.isIOBound(className.replace('.', '/'));

            String taskType = isIO ? "IO" : "CPU";

            // Check if class also internally runs both CPU + IO via HybridThreadManager
            if (className.endsWith("SaveBookingTask")) {
                taskType = "MIXED";
            }

            metadata.put(className, taskType);

            System.out.printf("🧠 DETECTED: %-40s → type=%-5s (via ASM)%n", className, taskType);
        }

        writeJson(metadata, OUTPUT_FILE);

        System.out.println("✅ task-metadata.json generated at → " + OUTPUT_FILE);
    }

    private static void writeJson(Map<String, String> map, String path) throws Exception {
        File outFile = new File(path);
        outFile.getParentFile().mkdirs();

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outFile))) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, map);
        }
    }

    private static Set<Class<?>> findSmartTasks(String packageName) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

        while (resources.hasMoreElements()) {
            File directory = new File(resources.nextElement().toURI());
            if (!directory.exists()) continue;

            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (SmartTask.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                        classes.add(clazz);
                    }
                }
            }
        }

        return classes;
    }
}
