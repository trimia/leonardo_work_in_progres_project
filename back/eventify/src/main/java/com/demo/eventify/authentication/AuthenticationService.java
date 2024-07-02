package com.demo.eventify.authentication;

import com.demo.eventify.config.JwtAuthenticationFilter;
import com.demo.eventify.config.JwtService;
import com.demo.eventify.controller.AdminController;
import com.demo.eventify.event.EventEntity;
import com.demo.eventify.event.EventEntity$;
import com.demo.eventify.login.LoginRequest;
import com.demo.eventify.token.Token;
import com.demo.eventify.token.TokenRepository;
import com.demo.eventify.token.TokenType;
import com.demo.eventify.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JPAStreamer jpaStreamer;

    private final UserRepository userRepository;
    private UserEntity userEntity;

  /*
    //////////////////// se ci passassero le informazioni direttamente dal
     front/login potremmo gia' avere un utente completo nel DB
   */

    public boolean isUserAboveMinimumAge(LocalDate birthdate, int minimumAge) {
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(birthdate, currentDate);
        int userAge = age.getYears();
        return userAge >= minimumAge;
    }

    public boolean fieldsAreValid(RegisterRequest request) {

        String firstname = request.getFirstname();
        String lastname = request.getLastname();
        String email = request.getEmail();
        String psw = request.getPassword();
        LocalDate birth = request.getDate_of_birth();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || psw.isEmpty()) ////// age.isEmpty missing
            return false;

        if (firstname.length() < 3 || firstname.length() > 20)
            return false;

        if (lastname.length() < 3 || lastname.length() > 20)
            return false;

//////// Logic to check userAge theoretically implemented
// still misses field in RegisterRequest/UserEntity
// because not sure on how to reach the field from frontend
        boolean minimumAge = isUserAboveMinimumAge(birth, 18);
        if (!minimumAge)
            return false;

        if (!email.endsWith(".it") && !email.endsWith(".com")) ////////// Only ".it/.com" allowed
            return false;

        String chiocciola = email.substring(email.indexOf("@"));
        if (!chiocciola.startsWith("@")) ///////// Decided before "@" you must put at least 5 letters
            return false;

//    MAIL_PROVIDER REGEX:
//    At least 3 characters.
//    Maximum 6 characters.
//    No "@" allowed.
//    No uppercase allowed.
        String mailProvider = email.substring(email.indexOf("@") + 1, email.lastIndexOf("."));
        String emailRegex = "^[a-z0-9]{3,10}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(mailProvider);
        if (!matcher.matches())
            return false;

//    PSW REGEX:
//    At least 8 characters.
//    Maximum 30 characters.
//    At least one uppercase letter.
//    At least one lowercase letter.
//    At least one special character.
//    At least one number.
        String pswRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,30}$";
        Pattern pswPattern = Pattern.compile(pswRegex);
        Matcher pswMatcher = pswPattern.matcher(psw);
        if (!pswMatcher.matches())
            return false;

        return true;
    }

    public boolean checkPsw(String psw) {
        String pswRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,30}$";
        Pattern pswPattern = Pattern.compile(pswRegex);
        Matcher pswMatcher = pswPattern.matcher(psw);
        return pswMatcher.matches();
    }

    public boolean userAlredyRegistered(RegisterRequest request) {
        List<UserEntity> users = jpaStreamer.stream(UserEntity.class).filter(UserEntity$.email.isNotNull()).toList();

        for (UserEntity user : users) {
            if (users.get(user.getId().intValue() - 1).getEmail().equals(request.getEmail()))
                return true;
        }
        return false;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserEntity.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .image(request.getImage())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        /////////////////// if user exists continue else return shit
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse homePage(AuthenticationRequest request) {
        try {
//      jwtAuthenticationFilter.doFilter(request, );
            return authenticate(request);
        } catch (Exception e) {
            System.out.println(e + " Cannot authorize transaction");
            return null;
        }
    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /*
      check this function together to understand if it alredy check the token from header from front?
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                System.out.println("******service refresh**********" + jwtService.isTokenValid(refreshToken, user));
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public AuthenticationResponse errorHandling(String str) {
        return AuthenticationResponse.builder().error(str).build();
    }

    @Transactional
    public AuthenticationResponse deleteUser(AuthenticationRequest request) throws UserNotFoundException {
        UserEntity user = repository.findUserByEmail(request.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User with email " + request.getEmail() + " not found");
        }
        List<Token> tokens = tokenRepository.findByUserId(user.getId());
        tokenRepository.deleteAll(tokens);
        userRepository.delete(user);

        return AuthenticationResponse.builder().build();
    }
}
