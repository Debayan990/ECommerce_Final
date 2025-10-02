package com.cts.controller;

import com.cts.dtos.AuthResponse;
import com.cts.dtos.LoginDto;
import com.cts.dtos.RegisterDto;
import com.cts.dtos.TokenValidationResponse;
import com.cts.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = {"/login", "/signin"})
    public AuthResponse login(@Valid @RequestBody LoginDto loginDto){
        String token =  authService.login(loginDto);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        return response;
    }

    @PostMapping(path = {"/register", "/signup"})
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto){
        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);
    }

    @GetMapping("/user/{username}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable String username) {
        List<String> roles = authService.getUserRoles(username);
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody String token) {
        try {
            String username = authService.validateTokenAndGetUsername(token);
            List<String> roles = authService.getRolesFromToken(token);
            return ResponseEntity.ok(new TokenValidationResponse(true, username, roles, null));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new TokenValidationResponse(false, null, null, e.getMessage()));
        }
    }
}