package com.railway.reservation.model;

import jakarta.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String user;

    private String trainId;
    private String src;
    private String dest;
    private int age;

    @Column(name = "booking_time")
    private Long time;

    private double fare;

    public Booking() {
        // No-arg constructor for JPA
    }

    public Booking(String user, String trainId, String src, String dest, int age, Long time, double fare) {
        this.user = user;
        this.trainId = trainId;
        this.src = src;
        this.dest = dest;
        this.age = age;
        this.time = time;
        this.fare = fare;
    }

    // ✅✅✅ GETTERS for JSON response
    public Long getId() { return id; }
    public String getUser() { return user; }
    public String getTrainId() { return trainId; }
    public String getSrc() { return src; }
    public String getDest() { return dest; }
    public int getAge() { return age; }
    public Long getTime() { return time; }
    public double getFare() { return fare; }

    // ✅ Builder pattern (unchanged)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String user;
        private String trainId;
        private String src;
        private String dest;
        private int age;
        private Long time;
        private double fare;

        public Builder user(String user) { this.user = user; return this; }
        public Builder trainId(String trainId) { this.trainId = trainId; return this; }
        public Builder src(String src) { this.src = src; return this; }
        public Builder dest(String dest) { this.dest = dest; return this; }
        public Builder age(int age) { this.age = age; return this; }
        public Builder time(Long time) { this.time = time; return this; }
        public Builder fare(double fare) { this.fare = fare; return this; }

        public Booking build() {
            return new Booking(user, trainId, src, dest, age, time, fare);
        }
    }

    @Override
    public String toString() {
        return String.format("Booking[user=%s, train=%s, src=%s, dest=%s, age=%d, fare=%.2f, time=%d]",
                user, trainId, src, dest, age, fare, time);
    }
}
