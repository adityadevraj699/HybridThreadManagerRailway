package com.railway.reservation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Train {

    @Id
    private String id;

    private String src;
    private String dest;
    private int availableSeats;

    public Train() {}

    public Train(String id, String src, String dest, int availableSeats) {
        this.id = id;
        this.src = src;
        this.dest = dest;
        this.availableSeats = availableSeats;
    }

    public String getId() { return id; }
    public String getSrc() { return src; }
    public String getDest() { return dest; }
    public int getAvailableSeats() { return availableSeats; }

    public void setAvailableSeats(int seats) {
        this.availableSeats = seats;
    }
}
