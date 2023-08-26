package com.example.keycloakprovider.exceptions;

import org.springframework.http.HttpStatus;

public class FieldTooSmallException extends AppException {
    public FieldTooSmallException(String fieldName, Integer fieldSize) {super(fieldName + " length must be greater than " + fieldSize, HttpStatus.BAD_REQUEST);}
}
