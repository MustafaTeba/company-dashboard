package com.vaadin.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.vaadin.example.backend.CompanyDataRepository;
import com.vaadin.example.backend.TestData;

@SpringBootApplication
public class CompanydashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanydashboardApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(CompanyDataRepository repository) {
		return (args) -> {
			TestData.createTestCompanyData(repository);
		};
	}
}
