package com.netmarble.unity.debugger.app.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {

	@Value("${executor.corePoolSize}")
	private int CORE_POOL_SIZE;

	@Value("${executor.maxPoolSize}")
	private int MAX_POOL_SIZE;

	@Value("${executor.queueCapacity}")
	private int QUEUE_CAPACITY;

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(CORE_POOL_SIZE);
		executor.setMaxPoolSize(MAX_POOL_SIZE);
		executor.setQueueCapacity(QUEUE_CAPACITY);
		executor.initialize();
		return executor;
	}

}
