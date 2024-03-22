package com.example.penguinproject.controller;


import com.example.penguinproject.common.AuthenticationRequest;
import com.example.penguinproject.common.AuthenticationResponse;
import com.example.penguinproject.common.RegisterRequest;
import com.example.penguinproject.service.AuthService;
import com.example.penguinproject.service.AuthService2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthController {

    @Autowired
    private final AuthService2 authService;

    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse register(@RequestBody AuthenticationRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        return authService.refreshToken(request, response);
    }
}
