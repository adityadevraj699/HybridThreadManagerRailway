# Hybrid Thread Railway Reservation System

## Features
- Auto-thread management: CPU-bound → ForkJoin, I/O-bound → Virtual Threads
- Spring Boot REST API (`/book-ticket`)
- Connected to Railway PostgreSQL Cloud DB
- JMeter test plan included

## How to Run
1. Replace DB credentials in `application.properties`
2. Run `Application.java`
3. Load `jmeter/BookingTestPlan.jmx` into JMeter
4. Launch test and view results in `/results`

MIT License