package com.railway.reservation.service;

import com.railway.reservation.model.Train;
import com.railway.reservation.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainService {

    @Autowired
    private TrainRepository trainRepository;

    // Initialize 3 trains only if DB is empty
    @Autowired
    public void initializeTrains() {
        if (trainRepository.count() == 0) {
            trainRepository.saveAll(List.of(
                new Train("T101", "Delhi", "Patna", 50000),
                new Train("T102", "Delhi", "Dhanbad", 50000),
                new Train("T103", "Mumbai", "Delhi", 50000)
            ));
        }
    }

    // Get train by src/dest (used for fallback or display)
    public Train getTrain(String src, String dest) {
        return trainRepository.findBySrcIgnoreCaseAndDestIgnoreCase(src, dest);
    }

    // ✅ Get train by ID (for strict validation)
    public Train getTrainById(String trainId) {
        return trainRepository.findById(trainId).orElse(null);
    }

    // ✅ Book seat if available
    public synchronized boolean bookSeat(String trainId) {
        Train train = trainRepository.findById(trainId).orElse(null);
        if (train != null && train.getAvailableSeats() > 0) {
            train.setAvailableSeats(train.getAvailableSeats() - 1);
            trainRepository.save(train);
            return true;
        }
        return false;
    }

    // List all trains
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }
}
