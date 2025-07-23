package com.railway.reservation.service;

import com.railway.reservation.model.Booking;
import com.railway.reservation.repository.BookingRepository;
import com.railway.reservation.thread.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final HybridThreadManager manager;

    private final BookingRepository repository;
    private final TrainService trainService;

    @Autowired
    public BookingService(
            MeterRegistry meterRegistry,
            BookingRepository repository,
            TrainService trainService) {
        this.manager = new HybridThreadManager(meterRegistry);
        this.repository = repository;
        this.trainService = trainService;
    }

    public String processBooking(String user, String src, String dest, int age, String trainId) {
        try {
            FareCalculationTask fareTask = new FareCalculationTask(src, dest, age);
            manager.executeAuto(fareTask);
            Thread.sleep(50);
            double fare = fareTask.getResult();

            PaymentTask payTask = new PaymentTask(user, fare);
            manager.executeAuto(payTask);

            SaveBookingTask saveTask = new SaveBookingTask(manager, repository, trainService, user, src, dest, age, fare, trainId);
            manager.executeAuto(saveTask);

        } catch (Exception e) {
            return "❌ Booking failed: " + e.getMessage();
        }

        return "✅ Booking confirmed for " + user;
    }

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }
}
