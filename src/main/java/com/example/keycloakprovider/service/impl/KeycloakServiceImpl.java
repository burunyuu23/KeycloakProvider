package com.example.keycloakprovider.service.impl;

import com.example.keycloakprovider.dtos.UserDTO;
import com.example.keycloakprovider.dtos.requests.RegisterRequestDTO;
import com.example.keycloakprovider.dtos.responses.TokensResponseDTO;
import com.example.keycloakprovider.exceptions.*;
import com.example.keycloakprovider.service.IKeycloakService;
import com.example.keycloakprovider.util.KeycloakProvider;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
@Slf4j
@Data
public class KeycloakServiceImpl implements IKeycloakService {

    private Gson gson;
    private ModelMapper modelMapper;

    public KeycloakServiceImpl(Gson gson, ModelMapper modelMapper) {
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    /**
     * Metodo para listar todos los usuarios de Keycloak
     * @return List<UserRepresentation>
     */
    public List<UserRepresentation> findAllUsers(){
        return KeycloakProvider.getRealmResource()
                .users()
                .list();
    }

    @Value("${users-posts.add-user}")
    private String usersPostsCreateUserApi;
    /**
     * Metodo para crear un usuario en keycloak
     * @return String
     */
    public TokensResponseDTO createUser(@NonNull UserDTO userDTO) {

        int status = 0;
        UsersResource usersResource = KeycloakProvider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);


        if (Objects.equals(userDTO.getUsername(), "")) {
            throw new FieldEmptyException("Username");
        }
        if (Objects.equals(userDTO.getPassword(), "")) {
            throw new FieldEmptyException("Password");
        }
        if (userDTO.getPassword().length() <= 8) {
            throw new FieldTooSmallException("Password", 8);
        }
        if (Objects.equals(userDTO.getEmail(), "")) {
            throw new FieldEmptyException("Email");
        }
        if (Objects.equals(userDTO.getFirstName(), "")) {
            throw new FieldEmptyException("First name");
        }
        if (Objects.equals(userDTO.getLastName(), "")) {
            throw new FieldEmptyException("Last name");
        }

        if (!KeycloakProvider.findUserByUsername(userDTO.getUsername()).isEmpty()) {
            throw new UserExistException("username");
        }
        if (!KeycloakProvider.findUserByEmail(userDTO.getEmail()).isEmpty()) {
            throw new UserExistException("email");
        }

        Response response = usersResource.create(userRepresentation);
        status = response.getStatus();

        if (status == 201) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setValue(userDTO.getPassword());

            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = KeycloakProvider.getRealmResource();

            List<RoleRepresentation> rolesRepresentation = new ArrayList<>();

            if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
                rolesRepresentation = realmResource.roles()
                        .list()
                        .stream()
                        .filter(role -> userDTO.getRoles()
                                .stream()
                                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())
                                        && !roleName.equalsIgnoreCase("admin")))
                        .toList();
            }
            rolesRepresentation.add(realmResource.roles().get("user").toRepresentation());

            realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentation);


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + KeycloakProvider.getAdminToken());

            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(userId, userDTO.getBirthdate());

            String requestBody = gson.toJson(registerRequestDTO);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseUsers = restTemplate.postForEntity(usersPostsCreateUserApi, requestEntity, String.class);

            int statusUser = responseUsers.getStatusCode().value();

            if (statusUser != 200) {
                throw new SomethingWentWrongException();
            }
            AccessTokenResponse accessTokenResponse = KeycloakProvider.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
            return modelMapper.map(accessTokenResponse, TokensResponseDTO.class);
        } else {
            throw new SomethingWentWrongException();
        }
    }


    /**
     * Metodo para borrar un usuario en keycloak
     * @return void
     */
    public void deleteUser(String userId){
        KeycloakProvider.getUserResource()
                .get(userId)
                .remove();
    }


    /**
     * Metodo para actualizar un usuario en keycloak
     * @return void
     */
    public void updateUser(String userId, @NonNull UserDTO userDTO){

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userDTO.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setCredentials(Collections.singletonList(credentialRepresentation));

        UserResource usersResource = KeycloakProvider.getUserResource().get(userId);
        usersResource.update(user);
    }
}
