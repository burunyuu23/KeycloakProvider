package com.example.keycloakprovider.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;


@Value
@RequiredArgsConstructor
@Builder
public class UserDTO implements Serializable{
    String username;
    String email;
    String password;
    String firstName;
    String lastName;
    LocalDate birthdate;
    Set<String> roles;
}
