package com.demo.eventify.config;

import com.demo.eventify.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final LogoutHandler logoutHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
//                    auth.requestMatchers("api/v1/auth/exp").authenticated();
                    auth.requestMatchers("api/v1/auth/delete-user").authenticated();
                    auth.requestMatchers("api/v1/auth/refresh-token").authenticated();
//                    auth.requestMatchers("api/v1/auth/exp").authenticated();
                    auth.requestMatchers("/home").authenticated();
                    auth.requestMatchers("api/users/**").authenticated();
                    auth.requestMatchers("/api/event/**").authenticated();
                    auth.requestMatchers("/register").permitAll();
                    auth.anyRequest().permitAll();
                })
//                .exceptionHandling(exceptionHandling -> {
//                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
//                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
//                    });
//                        exceptionHandling.accessDeniedHandler((request, response, accessDeniedException) -> {
//                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "ACCESS DENIED");
//                    });
//                })
//             .oauth2Login(withDefaults())
                .formLogin(withDefaults())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, userDetailsService, tokenRepository),
                        UsernamePasswordAuthenticationFilter.class
                )
                .logout((logout) -> {
                    logout.logoutSuccessUrl("http://localhost:3000/login");
                    logout.logoutUrl("/logout")
                            .addLogoutHandler(logoutHandler)
                            .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                })
                .build();
    }


}
