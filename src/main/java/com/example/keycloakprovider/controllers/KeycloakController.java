package com.example.keycloakprovider.controllers;


import com.example.keycloakprovider.dtos.UserDTO;
import com.example.keycloakprovider.dtos.responses.TokensResponseDTO;
import com.example.keycloakprovider.dtos.responses.UserResponseDTO;
import com.example.keycloakprovider.service.IKeycloakService;
import com.example.keycloakprovider.util.KeycloakProvider;
import lombok.Data;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@Data
@RequestMapping("/user")
@CrossOrigin
public class KeycloakController {
    private final IKeycloakService keycloakService;
    private final ModelMapper modelMapper;

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> findAllUsers(){
        List<UserResponseDTO> userRepresentationList = keycloakService.findAllUsers().stream().map(user -> modelMapper.map(user, UserResponseDTO.class)).toList();
        return ResponseEntity.ok(userRepresentationList);
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<UserResponseDTO> searchUserByUsername(@PathVariable String username){
        List<UserRepresentation> userRepresentationList = KeycloakProvider.findUserByUsername(username);
        if (userRepresentationList.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(modelMapper.map(userRepresentationList.get(0), UserResponseDTO.class));
    }

    @PostMapping("/create")
    public ResponseEntity<TokensResponseDTO> createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {
        TokensResponseDTO response = keycloakService.createUser(userDTO);
        return ResponseEntity.created(new URI("/api/keycloak/user/create")).body(response);
    }

    @PreAuthorize("hasRole('user')")
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO){
        if (KeycloakProvider.isSameUser(userId) || KeycloakProvider.isAdmin())
            keycloakService.updateUser(userId, userDTO);
        else
            return ResponseEntity.badRequest().body("You are not this user!");
        return ResponseEntity.ok("User updated successfully");
    }

    @PreAuthorize("hasRole('user')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId){
        if (KeycloakProvider.isSameUser(userId) || !KeycloakProvider.isAdmin())
            keycloakService.deleteUser(userId);
        else
            return ResponseEntity.badRequest().body("You are not this user!");
        return ResponseEntity.noContent().build();
    }
}
