package com.example.penguinproject.service;

import com.example.penguinproject.common.AuthenticationRequest;
import com.example.penguinproject.common.AuthenticationResponse;
import com.example.penguinproject.common.RegisterRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService2 {
    public AuthenticationResponse register(RegisterRequest request);

    public AuthenticationResponse login(AuthenticationRequest request);

    public AuthenticationResponse refreshToken(HttpServletRequest request,
                                               HttpServletResponse response);
}
