package com.railway.reservation.profiling;

import com.railway.reservation.thread.SmartTask;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class ProfilingTaskWrapper extends SmartTask {

    private final SmartTask originalTask;

    public ProfilingTaskWrapper(SmartTask task) {
        this.originalTask = task;
    }

    @Override
    public void runTask() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();

        long cpuBefore = bean.getThreadCpuTime(tid);
        long start = System.nanoTime();

        originalTask.runTask();

        long end = System.nanoTime();
        long cpuAfter = bean.getThreadCpuTime(tid);

        long cpuTime = cpuAfter - cpuBefore;
        long wallTime = end - start;

        double ratio = (double) cpuTime / wallTime;

        String type;
        if (ratio > 0.7) {
            type = "CPU";
        } else if (ratio < 0.2) {
            type = "IO";
        } else {
            type = "MIXED";
        }

        TaskProfiler.saveToMetadata(originalTask.getClass().getName(), type);
    }

    // ✅ FIXED: public access modifier
    @Override
    public void logThread(String taskName) {
        super.logThread(taskName);
    }
    
    

}
