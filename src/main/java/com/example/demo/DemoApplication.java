package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.timgroup.statsd.StatsDClient;

@EnableAutoConfiguration
@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer{
	
	@Autowired
	static StatsDClient statsd;
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		statsd.incrementCounter("service.random");
	}

}
