package com.ii.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	
	/*
	 * 비동기 처리에 대한 설정
	 */

    @Override
    @Bean(name = "mailExecutor")
	public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();	// 비동기적으로 메일을 보내기 위한 쓰레드 풀 설정
        executor.setCorePoolSize(2);	// 최소 대기 풀 사이즈
        executor.setMaxPoolSize(5);		// 최대 풀 사이즈
        executor.setQueueCapacity(10);	// 스레드 풀이 꽉 차 실행이 불가능 할 때 큐에 대기시킴
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 대기 큐마저 꽉차면 이 executor를 호출한 쓰레드가 직접 처리하게함, 즉 비동기 처리가 불가능하게 되어 회원가입 대기 시간이 길어지나 예외가 발생하지 않고 메일 전송에 누락도 발생하지 않음
        executor.setThreadNamePrefix("Async MailExecutor-");	// 로그에 표시되는 쓰레드 이름 앞에 붙을 prefix
        executor.initialize();
        return executor;
    }
    
}