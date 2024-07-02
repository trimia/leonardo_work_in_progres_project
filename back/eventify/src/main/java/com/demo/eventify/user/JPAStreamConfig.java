package com.demo.eventify.user;

import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JPAStreamConfig {
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public JPAStreamer jpaStreamer() {
		return JPAStreamer.of(entityManagerFactory);
	}
}
