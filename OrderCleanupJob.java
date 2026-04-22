package com.debuglab.scheduler;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OrderCleanupJob {

    @Scheduled(cron = "0 */1 * * * *")
    public void run() {
        System.out.println("Cron job running...");
    }
}
