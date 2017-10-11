package de.beckers.members;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.beckers.members.filters.AuthFilter;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan
public class JetsApp {
	public static void main(String[] args) {
		SpringApplication.run(JetsApp.class, args);
	}

	@Bean
	public Filter authFilter() {
		return new AuthFilter();
	}
}
