package com.demo.eventify.user;

import com.demo.eventify.event.EventEntity;
import com.demo.eventify.user.UserEntity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Id> {
/*
    CustomQuery generally not need to be mapped but could call by front like /search/queryname
    a custom query could use a dto to send specific object to front
 */
//    @Query("SELECT first_name FROM UserEntity first_name WHERE first_name.firstName=?1")
    Optional<UserEntity> findByFirstName(String first_name);

    ////////////////////////// Is it a custom query?!
    Optional<UserEntity> findByEmail(String email);

    UserEntity findUserByEmail(String email);

    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByResetPswToken(String token);
//    Optional<List<UserEntity>> findUserEntityBySubscribedEvent(EventEntity event);
    List<UserEntity> findAllBySubscribedEvent(EventEntity event);

//    void updateAllByEmail(String email);
//    void  updateAllByAccountNonExpired();

//    void update();

//    String find

}
