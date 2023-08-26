package com.example.keycloakprovider.exceptions;

import org.springframework.http.HttpStatus;

public class FieldEmptyException extends AppException {
    public FieldEmptyException(String fieldName) {super(fieldName + " must be not empty", HttpStatus.BAD_REQUEST);}
}
