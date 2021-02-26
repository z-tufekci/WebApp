package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter
{	
	@Autowired
    DataSource dataSource;
    
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource).and().eraseCredentials(false);
    }
    
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity
                .csrf().disable()                
                // .authorizeRequests().anyRequest().authenticated()
                .authorizeRequests().antMatchers("/v1/user/self","/v1/user/self/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/books/*","/books").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/books/*","/books").hasRole("USER")
                .and()
                .httpBasic();
    }
    
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }
    
   
    
}

