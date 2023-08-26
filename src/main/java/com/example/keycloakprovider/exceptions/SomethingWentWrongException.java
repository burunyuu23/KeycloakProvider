package com.example.keycloakprovider.exceptions;

import org.springframework.http.HttpStatus;

public class SomethingWentWrongException extends AppException {
    public SomethingWentWrongException() {super("Something went wrong, please contact with the administrator!", HttpStatus.BAD_REQUEST);}
}
