package com.benchpress200.photique;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
public class LogTestController {

    @GetMapping
    public void printTestLog() {
        log.info("info");
        log.warn("warn");
        log.error("error");
    }
}
