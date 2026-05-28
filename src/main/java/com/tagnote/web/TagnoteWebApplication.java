package com.tagnote.web;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class TagnoteWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TagnoteWebApplication.class, args);
    }

}
