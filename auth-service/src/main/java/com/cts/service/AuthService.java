package com.cts.service;

import com.cts.dtos.LoginDto;
import com.cts.dtos.RegisterDto;

import java.util.List;


public interface AuthService {
    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);

    List<String> getUserRoles(String username);
    String validateTokenAndGetUsername(String token);
    List<String> getRolesFromToken(String token);
}