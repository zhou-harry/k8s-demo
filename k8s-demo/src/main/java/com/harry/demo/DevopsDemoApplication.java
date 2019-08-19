package com.harry.demo;

import com.harry.demo.jco.JcoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync//启动异步调用
@SpringBootApplication
@EnableConfigurationProperties(JcoProperties.class)
public class DevopsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsDemoApplication.class, args);
	}

}
