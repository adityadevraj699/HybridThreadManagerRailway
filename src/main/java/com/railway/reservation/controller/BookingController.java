package com.railway.reservation.controller;

import com.railway.reservation.model.Booking;
import com.railway.reservation.model.Train;
import com.railway.reservation.service.BookingService;
import com.railway.reservation.service.TrainService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TrainService trainService;

    @GetMapping("/")
    public String home() {
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