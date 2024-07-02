package com.demo.eventify.user;

import com.demo.eventify.event.EventEntity;
//import com.demo.eventify.Event./*EventRepository*/;
import com.demo.eventify.EventifyApplication;
import com.demo.eventify.authentication.AuthenticationRequest;
import com.demo.eventify.authentication.AuthenticationService;
import com.demo.eventify.authentication.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.demo.eventify.user.Role.ADMIN;
import static com.demo.eventify.user.Role.MANAGER;

@Configuration
public class UserConfig {

    @Bean
//    CommandLineRunner commandLineRunner1(UserRepository userRepository, EventRepository eventRepository, AuthenticationService service) {
    CommandLineRunner commandLineRunner1(UserRepository userRepository, AuthenticationService service) {
        return args -> {
            UserEntity user1 = new UserEntity("daniele", "test", "e@mail.com", LocalDate.of(1991, Month.JUNE, 11), "Secret1!");
            UserEntity user2 = new UserEntity("matteo", "ciao", "a@mail.com", LocalDate.of(1992, Month.AUGUST, 27), "Secret1!");
            UserEntity user3 = new UserEntity("roberto", "miao", "b@mail.com", LocalDate.of(2002, Month.MAY, 12), "Secret1!");
//            userRepository.saveAll(List.of(daniele, matteo, roberto));
            var daniele = RegisterRequest.builder().firstname(user1.getFirstName()).image("").lastname(user1.getLastName()).email(user1.getEmail()).password(user1.getPassword()).build();
            System.out.println("Register --> Daniele token: " + service.register(daniele).getAccessToken());

            var matteo = RegisterRequest.builder().firstname(user2.getFirstName()).image("").lastname(user2.getLastName()).email(user2.getEmail()).password(user2.getPassword()).build();
            System.out.println("Register --> Matteo token: " + service.register(matteo).getAccessToken());

            var rob = RegisterRequest.builder().firstname(user3.getFirstName()).image("").lastname(user3.getLastName()).email(user3.getEmail()).password(user3.getPassword()).build();
            System.out.println("Authenticate --> Rob token: " + service.register(rob).getAccessToken());

            var authDan = AuthenticationRequest.builder().email(daniele.getEmail()).password(daniele.getPassword()).build();
            System.out.println("Authenticate --> Daniele token: " + service.authenticate(authDan).getAccessToken());

            var authMat = AuthenticationRequest.builder().email(matteo.getEmail()).password(matteo.getPassword()).build();
            System.out.println("Authenticate --> Daniele token: " + service.authenticate(authMat).getAccessToken());

            var authRob = AuthenticationRequest.builder().email(rob.getEmail()).password(rob.getPassword()).build();
            System.out.println("Authenticate --> Daniele token: " + service.authenticate(authRob).getAccessToken());

            ////////////// Test for Home-Page
            System.out.println("Home-page --> Daniele token: " + service.homePage(authDan).getAccessToken());

            ////////////// Test for relational tables
//            eventRepository.save(new EventEntity(user1.getId(), "Party", "Cool party", user1));
        };
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service
    ) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .image("")
                    .email("admin@mail.com")
                    .password("Secret1!")
                    .role(ADMIN)
                    .build();
            System.out.println("Register --> Admin token: " + service.register(admin).getAccessToken());

            var adminAuth = AuthenticationRequest.builder()
                    .email(admin.getEmail())
                    .password(admin.getPassword())
                    .build();
            System.out.println("Auth --> Admin token: " + service.authenticate(adminAuth).getAccessToken());

            var manager = RegisterRequest.builder()
                    .firstname("Manager")
                    .lastname("Manager")
                    .image("")
                    .email("manager@mail.com")
                    .password("Secret1!")
                    .role(MANAGER)
                    .build();
            System.out.println("Manager token: " + service.register(manager).getAccessToken());

        };
    }
}
