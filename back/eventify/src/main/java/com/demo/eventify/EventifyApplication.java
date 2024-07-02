package com.demo.eventify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class EventifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventifyApplication.class, args);

	}

//	public static void main(String[] args) {
//		SpringApplication app = new SpringApplication(EventifyApplication.class);
//		app.setDefaultProperties(Collections
//				.singletonMap("server.port", "8080"));
//		app.run(args);
//	}

}
