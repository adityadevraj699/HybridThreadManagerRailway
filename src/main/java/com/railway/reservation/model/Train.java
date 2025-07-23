package com.railway.reservation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Train {
    @Id
    private String id;
    private String src;
    private String dest;
    private int availableSeats;
}
