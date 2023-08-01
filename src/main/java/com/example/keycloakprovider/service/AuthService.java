package com.example.keycloakprovider.service;

import com.example.keycloakprovider.dtos.requests.LoginRequestDTO;
import com.example.keycloakprovider.dtos.requests.RefreshTokenRequestDTO;
import com.example.keycloakprovider.dtos.responses.TokensResponseDTO;
import com.example.keycloakprovider.exceptions.LoginFailedException;
import com.example.keycloakprovider.util.KeycloakProvider;
import lombok.Data;
import org.keycloak.representations.AccessTokenResponse;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@Data
public class AuthService {
    private final ModelMapper modelMapper;

    public TokensResponseDTO login(@NonNull LoginRequestDTO loginRequestDTO) {
        AccessTokenResponse accessTokenResponse = null;

        boolean usernameExists = loginRequestDTO.getUsername() != null && !KeycloakProvider.findUserByUsername(loginRequestDTO.getUsername()).isEmpty();
        boolean emailExists =  loginRequestDTO.getEmail() != null && !KeycloakProvider.findUserByEmail(loginRequestDTO.getEmail()).isEmpty();

        try {
            if (usernameExists || emailExists) {
                if (usernameExists) {
                    accessTokenResponse = KeycloakProvider.authenticateUser(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
                } else {
                    accessTokenResponse = KeycloakProvider.authenticateUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
                }
            }
        } catch (Exception e) {
            throw new LoginFailedException(false);
        }

        if (accessTokenResponse == null) {
            throw new LoginFailedException(!(usernameExists || emailExists));
        }

        return modelMapper.map(accessTokenResponse, TokensResponseDTO.class);
    }

    public TokensResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        AccessTokenResponse accessTokenResponse = KeycloakProvider.refreshAccessToken(refreshTokenRequestDTO.getRefreshToken());

        return modelMapper.map(accessTokenResponse, TokensResponseDTO.class);
    }
}
