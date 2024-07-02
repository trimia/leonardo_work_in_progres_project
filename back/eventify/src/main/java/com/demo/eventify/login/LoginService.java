package com.demo.eventify.login;

import com.demo.eventify.authentication.AuthenticationService;
import com.demo.eventify.user.UserEntity;
import com.demo.eventify.user.UserNotFoundException;
import com.demo.eventify.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LoginService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
        UserEntity user = userRepository.findUserByEmail(email);
        if (user != null) {
            user.setResetPswToken(token);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public UserEntity getByResetPasswordToken(String token) {
        return userRepository.findUserEntityByResetPswToken(token);
    }

    public void updatePassword(UserEntity user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPswToken(null);
        userRepository.save(user);
    }
}




