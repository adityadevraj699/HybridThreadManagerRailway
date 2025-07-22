package com.railway.reservation.model;

import lombok.Data;

@Data
public class BookingRequest {
    private String user;
    private String src;
    private String dest;
    private int age;
}