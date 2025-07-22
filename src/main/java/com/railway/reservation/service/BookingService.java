package com.railway.reservation.service;

import com.railway.reservation.model.Booking;
import com.railway.reservation.repository.BookingRepository;
import com.railway.reservation.thread.HybridThreadManager;
import com.railway.reservation.thread.SmartTask;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final HybridThreadManager manager = new HybridThreadManager();

    @Autowired
    private BookingRepository repository;

    public String processBooking(String user, String src, String dest, int age) {

        // 🔧 Task: Fare Calculation (CPU-bound)
        manager.executeAuto(new SmartTask() {
            @Override
            public void runTask() {
                System.out.println("🔧 Calculating fare on: " + Thread.currentThread());
                double fare = simulateFareCalculation(src, dest, age);

                // 💾 Task: Save to DB (I/O-bound)
                manager.executeAuto(new SmartTask() {
                    @Override
                    public void runTask() {
                        System.out.println("💾 Saving booking on: " + Thread.currentThread());
                        saveBooking(user, src, dest, age, fare);
                    }
                });
            }
        });

        return "✅ Booking request accepted for " + user;
    }

    public List<Booking> getAllBookings() {
        return repository.findAll(); // I/O-bound but outside thread manager for now
    }

    private double simulateFareCalculation(String src, String dest, int age) {
        return (src.length() + dest.length()) * 10 * (age > 60 ? 0.5 : 1.0); // 🧠 CPU logic
    }

    private void saveBooking(String user, String src, String dest, int age, double fare) {
        Booking booking = Booking.builder()
                .user(user)
                .src(src)
                .dest(dest)
                .age(age)
                .fare(fare)
                .build();

        repository.save(booking); // 💾 DB I/O
        System.out.println("✅ Saved to DB: " + booking);
    }
}
