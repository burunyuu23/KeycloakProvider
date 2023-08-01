package com.example.keycloakprovider;

import com.example.keycloakprovider.dtos.responses.ErrorResponseDTO;
import com.example.keycloakprovider.exceptions.AppException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KeycloakProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeycloakProviderApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<AppException, ErrorResponseDTO> typeMapException = modelMapper.createTypeMap(AppException.class, ErrorResponseDTO.class);
        typeMapException.addMappings(mapper -> {
            mapper.map(AppException::getMessage, ErrorResponseDTO::setMessage);
            mapper.map(AppException::getStatus, ErrorResponseDTO::setStatus);
        });

        return modelMapper;
    }
}
