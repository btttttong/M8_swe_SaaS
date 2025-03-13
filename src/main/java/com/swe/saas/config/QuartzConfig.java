package com.swe.saas.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail gitHubPollingJobDetail() {
        return JobBuilder.newJob(com.swe.saas.scheduler.GitHubPollingJob.class)
                .withIdentity("gitHubPollingJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger gitHubPollingJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(gitHubPollingJobDetail())
                .withIdentity("gitHubPollingTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(15)
                        .repeatForever())
                .build();
    }
}