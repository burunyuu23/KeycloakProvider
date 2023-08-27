package com.example.keycloakprovider.service;

import com.example.keycloakprovider.dtos.UserDTO;
import com.example.keycloakprovider.dtos.requests.RegisterRequestDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IKeycloakService {

    List<UserRepresentation> findAllUsers();
    String createUser(UserDTO userDTO);
    void deleteUser(String userId);
    void updateUser(String userId, UserDTO userDTO);
}
