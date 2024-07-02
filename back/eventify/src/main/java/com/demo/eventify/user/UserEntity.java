package com.demo.eventify.user;

import com.demo.eventify.event.EventEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.demo.eventify.token.Token;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Columns;
import org.hibernate.type.descriptor.java.DateJavaType;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Date;

import java.util.List;

//lombok automate getter setter and constructor
//@Getter
//@Setter
//@RequiredArgsConstructor
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
@Entity
@Table
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /*
    @JsonProperty
     i use it to specify and define jason property of single var
     @jsonProperty("first_name")
     @jsonProperty(name = "first_name",access=JsonProperty.Acces.WRITE_ONLY)
     @jsonignore

     */

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    //    private LocalDate dob;

    @Transient
    private Integer age;

    @JsonIgnore
    private String password;
    @JsonIgnore
    private String resetPswToken;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(columnDefinition = "text", nullable = false)
    private String image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("token")
    @JsonAlias({"token"})
    @JsonIgnoreProperties("user")
    private List<Token> tokens;

    @OneToMany(mappedBy = "owner")
    @JsonIgnoreProperties("user")
    private List<EventEntity> createdEvent;

    @ManyToMany(mappedBy = "subscribed")
    private List<EventEntity> subscribedEvent;


    public UserEntity(String firstName, String lastName, String email, LocalDate dob, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
//        this.dob = dob;
        this.password = password;
//        this.event = event;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
        ///////////// return role.getAuthorities();
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
//                ", dob=" + dob +
                ", age=" + age +
                ", password='" + password + '\'' +
                '}';
    }
}