package com.example.keycloakprovider.service.impl;

import com.example.keycloakprovider.dtos.requests.UserDTO;
import com.example.keycloakprovider.exceptions.*;
import com.example.keycloakprovider.service.IKeycloakService;
import com.example.keycloakprovider.util.KeycloakProvider;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService {

    /**
     * Metodo para listar todos los usuarios de Keycloak
     * @return List<UserRepresentation>
     */
    public List<UserRepresentation> findAllUsers(){
        return KeycloakProvider.getRealmResource()
                .users()
                .list();
    }

    /**
     * Metodo para crear un usuario en keycloak
     * @return String
     */
    public String createUser(@NonNull UserDTO userDTO) {

        int status = 0;
        UsersResource usersResource = KeycloakProvider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);


        if (Objects.equals(userDTO.getUsername(), "")) {
            throw new FieldEmptyException("Username");
        }
        if (Objects.equals(userDTO.getPassword(), "")) {
            throw new FieldEmptyException("Password");
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

            return "User created successfully!!";
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
