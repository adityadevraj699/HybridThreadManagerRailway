package com.railway.reservation.controller;

import com.railway.reservation.model.Booking;
import com.railway.reservation.model.BookingRequest;
import com.railway.reservation.service.BookingService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String home() {
        return "✅ Hybrid Thread Railway Booking App is Running!";
    }

    // ✅ New API to fetch all bookings from DB
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }


@PostMapping("/book-ticket")
public String bookTicket(@RequestParam String user, @RequestParam String src,
                         @RequestParam String dest, @RequestParam int age) {
    return bookingService.processBooking(user, src, dest, age);
}
}



