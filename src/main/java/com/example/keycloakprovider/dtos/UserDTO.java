package com.example.keycloakprovider.dtos;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

@Value
@RequiredArgsConstructor
@Builder
public class UserDTO implements Serializable {

    String username;
    String email;
    String firstName;
    String lastName;
    String password;
    Set<String> roles;
}
