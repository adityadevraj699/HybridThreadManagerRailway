// 📁 File: SaveBookingTask.java
package com.railway.reservation.thread;

import com.railway.reservation.model.Booking;
import com.railway.reservation.repository.BookingRepository;
import com.railway.reservation.service.TrainService;

import java.util.concurrent.StructuredTaskScope;

public class SaveBookingTask extends SmartTask {

    private final HybridThreadManager manager;
    private final BookingRepository repository;
    private final TrainService trainService;
    private final String user, src, dest, trainId;
    private final int age;
    private final double fare;

    public SaveBookingTask(HybridThreadManager manager, BookingRepository repository, TrainService trainService,
                           String user, String src, String dest, int age, double fare, String trainId) {
        this.manager = manager;
        this.repository = repository;
        this.trainService = trainService;
        this.user = user;
        this.src = src;
        this.dest = dest;
        this.age = age;
        this.fare = fare;
        this.trainId = trainId;
    }

    @Override
    public void runTask() {
        logThread("Save Booking [MIXED]");

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            var seatBookingFuture = scope.fork(() -> {
                manager.runCpu(() -> {
                    System.out.println("⚙️ [CPU SUB-TASK] Thread: " + Thread.currentThread());
                    if (!trainService.bookSeat(trainId)) {
                        throw new RuntimeException("No seats available for train " + trainId);
                    }
                });
                return null;
            });

            var saveBookingFuture = scope.fork(() -> {
                manager.runIo(() -> {
                    System.out.println("💾 [IO SUB-TASK] Thread: " + Thread.currentThread());
                    Booking booking = Booking.builder()
                            .user(user)
                            .src(src)
                            .dest(dest)
                            .age(age)
                            .fare(fare)
                            .trainId(trainId)
                            .time(System.currentTimeMillis()) // ✅ required by constructor
                            .build();

                    repository.save(booking);
                    System.out.println("✅ Saved to DB: " + booking);
                });
                return null;
            });

            scope.join();
            scope.throwIfFailed();

        } catch (Exception e) {
            throw new RuntimeException("❌ Error in SaveBookingTask: " + e.getMessage(), e);
        }
    }
}
