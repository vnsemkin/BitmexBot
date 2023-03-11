package bitmex.bitmexspring.services;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class PingTaskScheduler {
        private final ThreadPoolTaskScheduler taskScheduler;

        public PingTaskScheduler(){
            this.taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setPoolSize(1);
        }

    public ThreadPoolTaskScheduler getTaskScheduler() {
        return taskScheduler;
    }
}
