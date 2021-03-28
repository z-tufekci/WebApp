package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

@Configuration
public class AppConfig {
	
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername(System.getProperty("spring.datasource.username")); 
        dataSource.setPassword(System.getProperty("spring.datasource.password"));
        dataSource.setUrl(System.getProperty("spring.datasource.url")); 
        
        return dataSource;
    }
}