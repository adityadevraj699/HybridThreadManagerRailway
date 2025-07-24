package com.railway.reservation.controller;

import com.railway.reservation.model.Booking;
import com.railway.reservation.model.Train;
import com.railway.reservation.service.BookingService;
import com.railway.reservation.service.TrainService;
import com.railway.reservation.thread.HybridThreadManager;
import com.railway.reservation.thread.SmartTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class BookingController {

    private static final Logger logger = Logger.getLogger(BookingController.class.getName());

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TrainService trainService;

    @Autowired
    private HybridThreadManager threadManager;

    @GetMapping("/")
    public String home() {
        threadManager.executeAuto(new SmartTask() {
            @Override
            public void runTask() {
                logThread("HomeHiddenIOTask");
                try {
                    Thread.sleep(500); // simulate I/O
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public String getTaskName() {
                return "com.railway.reservation.thread.HomeHiddenIOTask";
            }
        });

        return "✅ Hybrid Thread Railway Booking App is Running!";
    }

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/trains")
    public List<Train> getAllTrains() {
        return trainService.getAllTrains();
    }

    @PostMapping("/book-ticket")
    public String bookTicket(@RequestParam String user,
                             @RequestParam String src,
                             @RequestParam String dest,
                             @RequestParam int age,
                             @RequestParam String trainId) {
        return bookingService.processBooking(user, src, dest, age, trainId);
    }
}
