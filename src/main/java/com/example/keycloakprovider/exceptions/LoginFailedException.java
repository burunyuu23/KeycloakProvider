package com.example.keycloakprovider.exceptions;

import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;

public class LoginFailedException extends AppException {
    private static final String incorrectUsernameMessage = "Incorrect username or email";
    private static final String incorrectPasswordMessage = "Incorrect password";
    public LoginFailedException(boolean incorrectUsername) {super(incorrectUsername ? incorrectUsernameMessage : incorrectPasswordMessage, HttpStatus.UNAUTHORIZED);}
}
