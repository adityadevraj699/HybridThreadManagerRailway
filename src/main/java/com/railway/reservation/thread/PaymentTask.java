package com.railway.reservation.thread;

public class PaymentTask extends SmartTask {
    private final String user;
    private final double fare;

    public PaymentTask(String user, double fare) {
        this.user = user;
        this.fare = fare;
    }

    @Override
    public void runTask() {
        logThread("Payment [IO]");
        try {
            Thread.sleep(100);
            System.out.println("💳 Payment processed for " + user + " amount ₹" + fare);
        } catch (InterruptedException e) {
            throw new RuntimeException("Payment failed");
        }
    }
}
