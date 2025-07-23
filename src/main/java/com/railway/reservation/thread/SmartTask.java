package com.railway.reservation.thread;

public abstract class SmartTask {
    public abstract void runTask();

    protected void logThread(String taskName) {
        System.out.println("🧵 Task [" + taskName + "] is running on thread: " + Thread.currentThread());
    }
}