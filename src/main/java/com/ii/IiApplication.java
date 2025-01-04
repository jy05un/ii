package com.ii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@PropertySource("classpath:application.yml")
@SpringBootApplication
public class IiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IiApplication.class, args);
	}

}
