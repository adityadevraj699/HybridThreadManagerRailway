// 📁 File location: src/main/java/com/railway/reservation/thread/SmartTask.java
package com.railway.reservation.thread;

public abstract class SmartTask {
    public abstract void runTask();

    public String getTaskName() {
        return this.getClass().getName(); // ✅ default implementation
    }

    public void logThread(String taskName) {
        System.out.println("🧵 Task [" + taskName + "] is running on thread: " + Thread.currentThread());
    }
}
