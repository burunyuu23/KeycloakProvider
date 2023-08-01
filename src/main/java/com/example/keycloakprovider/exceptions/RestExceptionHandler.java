package com.example.keycloakprovider.exceptions;

import com.example.keycloakprovider.dtos.responses.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {
    private final ModelMapper modelMapper;

    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<ErrorResponseDTO> handleAppException(AppException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(modelMapper.map(exception, ErrorResponseDTO.class));
    }
}
