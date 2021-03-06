package com.example.demo;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class AppConfig {
	
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
       // dataSource.setUsername("newuser");
       // dataSource.setPassword("Austin2021.");
       // dataSource.setUrl("jdbc:mysql://localhost:3306/user_db?createDatabaseIfNotExist=true"); 
        dataSource.setUsername("csye6225");
        dataSource.setPassword("Austin2021.");
        dataSource.setUrl("jdbc:mysql://terraform-20210303100657962300000002.cf2mbe6ehiim.us-east-1.rds.amazonaws.com:3306/user_db?createDatabaseIfNotExist=true"); 
        
        return dataSource;
    }

}