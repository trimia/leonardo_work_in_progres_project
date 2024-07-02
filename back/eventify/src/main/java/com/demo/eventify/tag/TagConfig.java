package com.demo.eventify.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TagConfig {

	private final TagRepository tagRepository;

	@Bean
	public CommandLineRunner commandLineRunnerTag() {
		return args-> {
			var tag = TagEntity.builder()
					.tag("party")
					.build();
			var tag1 = TagEntity.builder()
					.tag("bomba")
					.build();
			var tag2 = TagEntity.builder()
					.tag("superparty")
					.build();
			var tag3 = TagEntity.builder()
					.tag("boooo")
					.build();
			var tag4 = TagEntity.builder()
					.tag("nice")
					.build();
			tagRepository.saveAll(List.of(tag, tag1, tag2, tag3, tag4));
		};
	}
}
