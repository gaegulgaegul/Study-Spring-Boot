package com.workspace.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.workspace.rest.event.BoardEventHandler;

@SpringBootApplication
public class DataRestCustomApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataRestCustomApplication.class, args);
	}
	
	@Bean
	BoardEventHandler boardEventHandler() {
		return new BoardEventHandler();
	}
	
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@EnableWebSecurity
	static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		
		@Bean
		InMemoryUserDetailsManager userDetailsManager() {
			User.UserBuilder commonUser = User.withUsername("commonUser")
					.password("{noop}common").roles("USER");
			User.UserBuilder havi = User.withUsername("havi").password("{noop}test").roles("USER", "ADMIN");
			
			List<UserDetails> userDetailsList = new ArrayList<>();
			userDetailsList.add(commonUser.build());
			userDetailsList.add(havi.build());
			
			return new InMemoryUserDetailsManager(userDetailsList);
		}
		
	}

}

