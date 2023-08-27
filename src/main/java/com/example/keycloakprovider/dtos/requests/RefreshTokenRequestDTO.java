package com.example.keycloakprovider.dtos.requests;

import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    private String refreshToken;
}
