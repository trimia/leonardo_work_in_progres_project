package com.demo.eventify.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserEntity toUser(UserDto userDto) {
        if(userDto==null){
            return null;
        }
        UserEntity.UserEntityBuilder userEntity=UserEntity.builder();
        userEntity.id(userDto.getId());
        userEntity.firstName(userDto.getFirstName());
        userEntity.lastName(userDto.getLastName());
        userEntity.email(userDto.getEmail());
        return userEntity.build();
    }

    @Override
    public UserDto UserToUserDto(Optional<UserEntity> user) {
        if (user.isEmpty()){
            return null;
        }
        UserDto userDto=new UserDto();
        userDto.setId((user.get().getId()));
        userDto.setFirstName(user.get().getFirstName());
        userDto.setLastName(user.get().getLastName());
        userDto.setEmail(user.get().getEmail());
        userDto.setImage(user.get().getImage());
        return userDto;
    }

    @Override
    public List<UserEntity> toUserList(List<UserDto> userDtoList) {
        if(userDtoList==null){
            return null;
        }
        List<UserEntity> list= new ArrayList<>(userDtoList.size());
        for (UserDto userDto : userDtoList) {
            list.add(toUser(userDto));
        }
        return list;
    }

    @Override
    public List<UserDto> UserListToUserDto(List<UserEntity> user) {
        if(user==null){
            return null;
        }
        List<UserDto>list =new ArrayList<>(user.size());
        for (UserEntity userEntity : user) {
            list.add(UserToUserDto(Optional.of(userEntity)));
        }
        return list;
    }
}
