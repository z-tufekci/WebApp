package com.example.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableAutoConfiguration
@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
