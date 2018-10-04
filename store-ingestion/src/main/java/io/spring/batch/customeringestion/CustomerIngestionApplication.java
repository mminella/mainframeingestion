package io.spring.batch.customeringestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@EnableTask
@SpringBootApplication
public class CustomerIngestionApplication {

	public static void main(String[] args) {

		SpringApplication.run(CustomerIngestionApplication.class, args);
	}
}
