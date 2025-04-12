package com.benchpress200.photique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class PhotiqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotiqueApplication.class, args);
    }

}
