package com.example.keycloakprovider.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
public class AppException extends RuntimeException{
    private final HttpStatus status;

    @JsonCreator
    public AppException(@JsonProperty("message") String message,
                        @JsonProperty("status") HttpStatus status) {
        super(message);
        this.status = status;
    }

    public String getMessage(){
        return super.getMessage();
    }
}
