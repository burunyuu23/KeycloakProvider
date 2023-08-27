package com.example.keycloakprovider.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
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
    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("last_name")
    String lastName;
    LocalDate birthdate;
    Set<String> roles;
}
