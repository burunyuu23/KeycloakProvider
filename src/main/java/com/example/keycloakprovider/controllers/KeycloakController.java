package com.example.keycloakprovider.controllers;


import com.example.keycloakprovider.dtos.UserDTO;
import com.example.keycloakprovider.dtos.requests.RegisterRequestDTO;
import com.example.keycloakprovider.dtos.responses.TokensResponseDTO;
import com.example.keycloakprovider.service.IKeycloakService;
import com.example.keycloakprovider.util.KeycloakProvider;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class KeycloakController {

    private final IKeycloakService keycloakService;

    public KeycloakController(IKeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PreAuthorize("hasRole('user')")
    @GetMapping("/search")
    public ResponseEntity<?> findAllUsers(){
        return ResponseEntity.ok(keycloakService.findAllUsers());
    }

    @PreAuthorize("hasRole('user')")
    @GetMapping("/search/{username}")
    public ResponseEntity<?> searchUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(KeycloakProvider.findUserByUsername(username));
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
