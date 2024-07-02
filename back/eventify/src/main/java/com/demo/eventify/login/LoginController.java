package com.demo.eventify.login;

import com.demo.eventify.authentication.AuthenticationService;
import com.demo.eventify.config.JwtService;
import com.demo.eventify.event.EventRequest;
import com.demo.eventify.user.UserEntity;
import com.demo.eventify.user.UserNotFoundException;
import com.demo.eventify.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;

@Controller
public class LoginController {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/forgot_password")
    public ResponseEntity<String> processForgotPassword( @RequestBody LoginRequest request) {

        String email = request.getEmail();
        UserEntity user = userRepository.findUserByEmail(email);
        String token = jwtService.generateFirstResetToken(user);
        String link = "http://localhost:3000/reset_password";

        try {
            loginService.updateResetPasswordToken(token, email);

            String resetPasswordLink = link + "?token=" + token;
            try{
            sendEmail(email, resetPasswordLink);}catch(SendFailedException e){
                return new ResponseEntity<>("mail not valid",HttpStatus.UNAUTHORIZED);

            }
            return new ResponseEntity<>("mail sent", HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        } catch (UnsupportedEncodingException | MessagingException | SocketException e) {
            return new ResponseEntity<>("error mail not send", HttpStatus.I_AM_A_TEAPOT);
        }
    }

    public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException, SocketException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("eventy42@libero.it", "Eventify Support");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>" + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + link + "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password, " + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);

    }

    @PostMapping("/test_p")
    public ResponseEntity<String> verifytoken(@RequestBody LoginRequest request) throws UserNotFoundException {
        String token = request.getToken();
        UserEntity user = userRepository.findUserEntityByResetPswToken(token);
        if (user != null && !jwtService.isResetTokenExpired(token)) {
            String resetToken = jwtService.generateResetToken(user);
            loginService.updateResetPasswordToken(resetToken, user.getEmail());

            return new ResponseEntity<>(resetToken, HttpStatus.OK);
        } else return new ResponseEntity<>("invalid request", HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/reset_password")
    public ResponseEntity<String> processResetPassword(@RequestBody LoginRequest request) throws UserNotFoundException {
        String token = request.getToken();
        String password = request.getPassword();
        UserEntity user = userRepository.findUserEntityByResetPswToken(token);

        if (token != null && !jwtService.isResetTokenExpired(token)) {

            if (authenticationService.checkPsw(password)) {
                loginService.updatePassword(user, password);
                return new ResponseEntity<>("password successfully changed", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("inserted pasword is not valid",HttpStatus.NOT_ACCEPTABLE);
            }

        } else {
            return new ResponseEntity<>("token not present or valid do your choice", HttpStatus.NOT_FOUND);
        }

    }
}


