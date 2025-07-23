package com.railway.reservation.repository;

import com.railway.reservation.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRepository extends JpaRepository<Train, String> {
    Train findBySrcIgnoreCaseAndDestIgnoreCase(String src, String dest);
}