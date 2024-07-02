package com.demo.eventify.user;

//import org.springframework.security.core.userdetails.User;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

@Service
public class UserService {

    public UserEntity getUser(){
        return new UserEntity("dan", "test", "ciao", LocalDate.of(2000, Month.FEBRUARY, 15), "secret");
    }


}