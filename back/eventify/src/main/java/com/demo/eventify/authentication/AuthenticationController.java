package com.demo.eventify.authentication;

import com.demo.eventify.config.JwtService;
import com.demo.eventify.config.LogoutService;
import com.demo.eventify.event.EventEntity;
import com.demo.eventify.token.Token;
import com.demo.eventify.token.TokenRepository;
import com.demo.eventify.user.UserEntity;
import com.demo.eventify.user.UserNotFoundException;
import com.demo.eventify.user.UserRepository;
import io.swagger.v3.oas.annotations.headers.Header;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private LogoutService logoutService;
private UserEntity userEntity;
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
          @RequestBody RegisterRequest request
  ) {
   if(service.userAlredyRegistered(request))
      return new ResponseEntity<>(service.errorHandling("mail already present"), HttpStatus.UNAUTHORIZED);
    if (service.fieldsAreValid(request))
      return new ResponseEntity<>(service.register(request), HttpStatus.OK);
	return new ResponseEntity<>(service.errorHandling("registration failed"), HttpStatus.I_AM_A_TEAPOT);
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody AuthenticationRequest request
  ) {
    String email = request.getEmail();
    String psw = request.getPassword();
    if (!email.isEmpty() || !psw.isEmpty()) {
      return new ResponseEntity<>(service.authenticate(request), HttpStatus.OK);
    }
	return new ResponseEntity<>(null, HttpStatus.I_AM_A_TEAPOT);
  }

  @PostMapping("/home-page")
  public ResponseEntity<AuthenticationResponse> homePage(
          @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.homePage(request));
  }

  @GetMapping("/refresh-token")
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
    System.out.println("********************"+response);
  }
  @GetMapping("/cihairagionete")
  public ResponseEntity<String> ok() {
    return new ResponseEntity<>("HAI RAGIONE", HttpStatus.OK);
  }

//  @GetMapping("/exp")
//  public ResponseEntity<String>isExpired(){
////    if(jwtService.isTokenValid(token,userRepository.findUserByEmail(jwtService.extractUsername(token))))
//      return  new ResponseEntity<>("good job",HttpStatus.OK);
////    return new ResponseEntity<>("è andato a male",HttpStatus.GONE);
//  }

  @DeleteMapping("/delete_user")
  public ResponseEntity<AuthenticationResponse> deleteUser(@RequestBody AuthenticationRequest request) {
    try {
      return new ResponseEntity<>(service.deleteUser(request), HttpStatus.OK);
    } catch (UserNotFoundException e) {
      return new ResponseEntity<>(AuthenticationResponse.builder().error("USER NOT FOUND").build(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(AuthenticationResponse.builder().error("500 INTERNAL SERVER ERROR").build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
