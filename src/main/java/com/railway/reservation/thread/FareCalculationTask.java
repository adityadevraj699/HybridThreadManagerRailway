package com.railway.reservation.thread;

public class FareCalculationTask extends SmartTask {
    private final String src, dest;
    private final int age;
    private double result;

    public FareCalculationTask(String src, String dest, int age) {
        this.src = src;
        this.dest = dest;
        this.age = age;
    }

    public void runTask() {
        logThread("Fare Calculation [CPU]");
        result = (src.length() + dest.length()) * 10 * (age > 60 ? 0.5 : 1.0);
    }

    public double getResult() {
        return result;
    }
}