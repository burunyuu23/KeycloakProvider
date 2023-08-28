package com.example.keycloakprovider.controllers;

import com.example.keycloakprovider.dtos.requests.LoginRequestDTO;
import com.example.keycloakprovider.dtos.requests.RefreshTokenRequestDTO;
import com.example.keycloakprovider.dtos.responses.TokensResponseDTO;
import com.example.keycloakprovider.service.AuthService;
import lombok.Data;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Data
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokensResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        TokensResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokensResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        val responseMessage = authService.refreshToken(refreshTokenRequestDTO);
        return ResponseEntity.ok(responseMessage);
    }
}
