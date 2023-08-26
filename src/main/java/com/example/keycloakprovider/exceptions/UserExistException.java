package com.example.keycloakprovider.exceptions;

import org.springframework.http.HttpStatus;

public class UserExistException extends AppException {
    public UserExistException(String fieldName) {super(String.format("User with this %s exist already!", fieldName), HttpStatus.BAD_REQUEST);}
}
