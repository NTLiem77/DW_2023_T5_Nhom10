package com.example.dtwh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.dtwh.Scheduler"})
public class DtwhApplication {
    public static void main(String[] args) {
        SpringApplication.run(DtwhApplication.class, args);
    }
}


