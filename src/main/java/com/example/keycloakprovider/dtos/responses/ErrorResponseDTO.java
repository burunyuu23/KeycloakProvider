package com.example.keycloakprovider.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response with error")
public class ErrorResponseDTO {
    @Schema(
            description = "Error message",
            example = "Something not found")
    private String message;

    @Schema(
            description = "Error status",
            example = "404 NOT_FOUND")
    private HttpStatus status;
}
