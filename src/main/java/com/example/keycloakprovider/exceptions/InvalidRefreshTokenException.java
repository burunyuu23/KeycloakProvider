package com.example.keycloakprovider.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends AppException{

    public InvalidRefreshTokenException() {
        super("Invalid refresh token", HttpStatus.BAD_REQUEST);
    }
}
