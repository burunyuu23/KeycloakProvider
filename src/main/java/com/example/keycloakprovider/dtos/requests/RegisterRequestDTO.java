package com.example.keycloakprovider.dtos.requests;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

@Value
public class RegisterRequestDTO implements Serializable {
    String id;
    LocalDate birthdate;
}
