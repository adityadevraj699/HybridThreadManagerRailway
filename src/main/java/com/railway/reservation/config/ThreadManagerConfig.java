package com.railway.reservation.config;

import com.railway.reservation.thread.HybridThreadManager;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadManagerConfig {

    @Bean
    public HybridThreadManager threadManager(MeterRegistry registry) {
        return new HybridThreadManager(registry);
    }
}
