package com.example.keycloakprovider.dtos;

import lombok.*;

import java.io.Serializable;
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
    Set<String> roles;
}
