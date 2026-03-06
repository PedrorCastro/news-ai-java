package com.newsai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * SchedulerConfig - Configura o TaskScheduler do Spring
 *
 * Necessario para que o NewsController possa reagendar
 * o envio de emails dinamicamente quando o usuario altera
 * o horario pela pagina web.
 */
@Configuration
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("news-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}