package com.demo.eventify.event;

import com.demo.eventify.authentication.AuthenticationService;
import com.demo.eventify.authentication.RegisterRequest;
import com.demo.eventify.tag.TagEntity;
import com.demo.eventify.tag.TagRepository;
import com.demo.eventify.user.UserEntity;
import com.demo.eventify.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class EventConfigurator {

	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final TagRepository tagRepository;
	private final AuthenticationService authenticationService;
@Autowired
	private PasswordEncoder passwordEncoder;
	@Bean
    public CommandLineRunner commandLineRunnerEvent() {
		var sdesdo = UserEntity.builder()
				.firstName("sdesdo1")
				.lastName("sdesdoLastname1")
				.image("")
				.email("s@mail.com")
				.password(passwordEncoder.encode("Secret1!"))
				.build();
		userRepository.save(sdesdo);
		var sdesdo4 = UserEntity.builder()
				.firstName("sdesdo4")
				.lastName("sdesdoLastname1")
				.image("")
				.email("dsa@mail.com")
				.password(passwordEncoder.encode("Secret1!"))
				.build();
		userRepository.save(sdesdo4);

		var sdesdone = new UserEntity("sdesdo2", "miao", "sdesdo@mail.com", LocalDate.of(2002, Month.MAY, 12), "Secret1!");
		var daniele = RegisterRequest.builder().firstname(sdesdone.getFirstName()).image("").lastname(sdesdone.getLastName()).email(sdesdone.getEmail()).password(sdesdone.getPassword()).build();
		System.out.println("Register --> DanielONE token: " + authenticationService.register(daniele).getAccessToken());


        return args -> {
			var tag2 = TagEntity.builder().tag("*bello").build();
			var tag3 = TagEntity.builder().tag("*pazzesco").build();
			var tag4 = TagEntity.builder().tag("*noschifo").build();
			Set<TagEntity> tag1 = new HashSet<>();
			tag1.add(tag2);
			tag1.add(tag3);
			tag1.add(tag4);

			var tag5 = TagEntity.builder().tag("*MILLIONAMARCH").build();
			var tag6 = TagEntity.builder().tag("*SERATABANLGA").build();
			var tag7 = TagEntity.builder().tag("*MUCCAASSASSINA").build();
			Set<TagEntity> tag11 = new HashSet<>();
			tag11.add(tag5);
			tag11.add(tag6);
			tag11.add(tag7);

			var tag8 = TagEntity.builder().tag("*BELLA").build();
			var tag9 = TagEntity.builder().tag("*PERDINCIBACCO").build();
			var tag10 = TagEntity.builder().tag("*BALLACOILUPI").build();
			Set<TagEntity> tag111 = new HashSet<>();
			tag111.add(tag8);
			tag111.add(tag9);
			tag111.add(tag10);
			tagRepository.saveAll(List.of(tag2, tag3, tag4, tag5, tag6, tag7, tag8, tag9, tag10));

			List<UserEntity>loro=new ArrayList<>();
			loro.add(sdesdo);
			loro.add(sdesdo4);
			var event1 = EventEntity.builder()
					.title("PARTY")
					.owner(sdesdo)
					.description("bellissimo")
					.image(new String[0])
					.datetime(LocalDateTime.of(2001, Month.SEPTEMBER, 11,8,13))
					.place("New York")
					.tags(tag1)
					.build();
			var event2 = EventEntity.builder()
					.owner(sdesdo)
					.title("CONCERT")
					.description("first concert of the year")
					.image(new String[0])
					.datetime(LocalDateTime.of(4000, Month.JANUARY, 1,23,30))
					.subscribed(loro)
					.place("London")
					.tags(tag11)
					.build();
			var event7 = EventEntity.builder()
					.owner(sdesdo)
					.title("TROPICAAL")
					.image(new String[0])
					.description("first concert of the year")
					.datetime(LocalDateTime.of(2003, Month.JANUARY, 1,23,30))
					.place("jhghjgjhgkhj")
					.tags(tag111)
					.build();

			var sdesdo2 = userRepository.findByEmail("sdesdo@mail.com").isPresent() ? userRepository.findByEmail("sdesdo@mail.com").get() : null;
//			System.out.println("///////////////////////////////////////////////////////nacoasSDESDO:////////////////////////////////////// " + sdesdo2);
			var event3 = EventEntity.builder()
					.owner(sdesdo2)
					.title("TOURNAMENT")
					.tags(tag11)
					.image(new String[0])
					.description("de cristo zi")
					.datetime(LocalDateTime.of(2000, Month.OCTOBER, 10,18,00))
					.place("New York")
					.build();
			eventRepository.saveAll(List.of(event1, event2, event3, event7));
        };
    }
}
