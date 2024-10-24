package com.wine.auth.controller;

import com.wine.auth.dto.*;
import com.wine.auth.exception.InvalidUserCredentialsException;
import com.wine.auth.exception.UserAlreadyExistsException;
import com.wine.auth.service.KeycloakAdminClientService;
import jakarta.validation.Valid;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakAdminClientService keycloakAdminClientService;


    public AuthController(KeycloakAdminClientService keycloakAdminClientService) {
        this.keycloakAdminClientService = keycloakAdminClientService;
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest user) throws UserAlreadyExistsException {
        return new ResponseEntity<>(keycloakAdminClientService.createKeycloakUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest loginRequest) throws InvalidUserCredentialsException {
        AccessTokenResponse response = keycloakAdminClientService.getAccessToken(loginRequest.getUsername(), loginRequest.getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestBody TokenRequest request) {
        return keycloakAdminClientService.logout(request);
    }

    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(@RequestBody TokenRequest token) {
        return keycloakAdminClientService.introspect(token);
    }



}

