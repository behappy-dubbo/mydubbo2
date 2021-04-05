package org.xiaowu.mydubbo.framework.pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author 小五
 */
public class ExecutorServicePool {

    /**
     * 自定义异步线程池
     *
     * @return
     */
    public static AsyncTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(50);
        executor.setKeepAliveSeconds(9000);
        executor.setQueueCapacity(800);
        executor.initialize();
        return executor;
    }
}
