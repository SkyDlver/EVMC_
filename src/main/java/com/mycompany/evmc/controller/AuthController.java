package com.mycompany.evmc.controller;

import com.mycompany.evmc.dto.LoginRequest;
import com.mycompany.evmc.dto.LoginResponse;
import com.mycompany.evmc.dto.RegisterRequest;
import com.mycompany.evmc.dto.RegisterResponse;
import com.mycompany.evmc.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint to register a new user.
     *
     * @param registerRequest The registration details provided by the client.
     * @return ResponseEntity containing the created user details.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }

    /**
     * Endpoint to log in a user and return a JWT token.
     *
     * @param loginRequest The login credentials provided by the client.
     * @return ResponseEntity containing the JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.loginUser(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken())
                .body(response);
    }
}