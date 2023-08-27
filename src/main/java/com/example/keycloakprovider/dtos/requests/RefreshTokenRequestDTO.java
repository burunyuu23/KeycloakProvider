package com.example.keycloakprovider.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    @JsonProperty("refresh_token")
    private String refreshToken;
}
