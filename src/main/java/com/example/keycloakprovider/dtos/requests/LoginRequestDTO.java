package com.example.keycloakprovider.dtos.requests;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Value
@RequiredArgsConstructor
@Builder
public class LoginRequestDTO implements Serializable {
    String username;
    String email;
    String password;
}
