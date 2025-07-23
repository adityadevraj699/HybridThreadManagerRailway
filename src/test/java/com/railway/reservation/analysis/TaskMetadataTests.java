package com.railway.reservation.analysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit Test to validate the correctness of generated task-metadata.json
 * Ensures all expected SmartTask entries are detected and classified correctly.
 */
public class TaskMetadataTests {

    @Test
    void taskMetadataShouldContainAllSmartTasks() throws Exception {
        // Load the metadata from the generated JSON file
        File jsonFile = new File("src/main/resources/task-metadata.json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> metadata = mapper.readValue(jsonFile, new TypeReference<>() {});

        // ✅ Assert expected tasks and types
        assertTrue(metadata.containsKey("com.railway.reservation.thread.PaymentTask"));
        assertEquals("IO", metadata.get("com.railway.reservation.thread.PaymentTask"));

        assertTrue(metadata.containsKey("com.railway.reservation.thread.FareCalculationTask"));
        assertEquals("CPU", metadata.get("com.railway.reservation.thread.FareCalculationTask"));

        assertTrue(metadata.containsKey("com.railway.reservation.thread.SaveBookingTask"));
        assertEquals("MIXED", metadata.get("com.railway.reservation.thread.SaveBookingTask"));
    }
}
