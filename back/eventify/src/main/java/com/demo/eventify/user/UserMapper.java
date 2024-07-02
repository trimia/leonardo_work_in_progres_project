package com.demo.eventify.user;
//import com.demo.eventify.user.UserDto;
//import com.demo.eventify.user.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMapper {
    UserEntity toUser(
            UserDto userDto
    );

    UserDto UserToUserDto(
            Optional<UserEntity> user
    );

    List<UserEntity> toUserList(
            List<UserDto> userDtoList
    );

    List<UserDto> UserListToUserDto(
            List<UserEntity> user
    );
}
