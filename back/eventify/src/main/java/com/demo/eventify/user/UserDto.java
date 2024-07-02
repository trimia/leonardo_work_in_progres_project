package com.demo.eventify.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/*
    a custom query could use a dto to send specific object to front
    dto is a class like an entity but is only for front not table or entity

 */
/*
    data from lombok create get set constructor
 */
@Data
public class UserDto {
    @JsonFormat
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String image;

}
