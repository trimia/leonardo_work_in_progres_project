package com.demo.eventify.user;

import com.demo.eventify.config.JwtService;
import com.demo.eventify.event.EventRepository;
import com.demo.eventify.event.EventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
//@CrossOrigin("http://localhost:3000/")
@RequestMapping(value = "api/users", produces = "application/json")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final EventRepository eventRepository;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, UserMapper userMapper, JwtService jwtService, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.eventRepository = eventRepository;
    }

//    public UserController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
 //8080/api/users/1?name=name
    @GetMapping("test")
    public String getUser(){
        return this.userService.getUser().toString();
    }

    @GetMapping("all")
    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }
    @GetMapping("mail")
    public ResponseEntity<Optional<UserDto>> getUserByEmail(@RequestHeader ("Authorization") String token) {
//        if (token != null ||token.startsWith("Bearer ")) {
//        }
        String jwt = token.substring(7);
        String email = jwtService.extractUsername(jwt);
        return new ResponseEntity<>(Optional.of(userMapper.UserToUserDto(userRepository.findByEmail(email))),HttpStatus.OK);
    }

    @GetMapping("findName")
    public Optional<UserEntity> findUserByName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }
    @PostMapping("/findSubscribedUser")
    public ResponseEntity<Optional<List<UserDto>>>getUserSubscribed(@RequestBody EventRequest request){
        return new ResponseEntity<>(Optional.of(userMapper.UserListToUserDto(userRepository.findAllBySubscribedEvent(eventRepository.findEventEntityById(request.getId())))), HttpStatus.OK);
    }
}
