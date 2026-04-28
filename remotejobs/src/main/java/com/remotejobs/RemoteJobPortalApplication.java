package com.remotejobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RemoteJobPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(RemoteJobPortalApplication.class, args);
    }
}