package com.railway.reservation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")  // ✅ Rename the column in DB
    private String user;

    private String src;
    private String dest;
    private int age;
    private double fare;
}

