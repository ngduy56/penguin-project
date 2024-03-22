package com.example.penguinproject.service;

import com.example.penguinproject.common.AuthenticationRequest;
import com.example.penguinproject.common.AuthenticationResponse;
import com.example.penguinproject.common.Constants;
import com.example.penguinproject.common.RegisterRequest;
import com.example.penguinproject.jwt.JwtService;
import com.example.penguinproject.model.Role;
import com.example.penguinproject.model.Token;
import com.example.penguinproject.model.TokenType;
import com.example.penguinproject.model.User;
import com.example.penguinproject.repository.TokenRepository;
import com.example.penguinproject.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService2 {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        try {
            Optional<User> userDb = userRepository.findByEmail(request.getEmail());
            if (userDb.isPresent()) {
                return AuthenticationResponse.builder()
                        .accessToken(null)
                        .refreshToken(null)
                        .message("Email has existed!")
                        .build();
            }
            User.UserBuilder userBuilder = User.builder();
            User user = userBuilder
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .createAt(new Date())
                    .build();
            User savedUser = userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .message("Register successfully!")
                    .build();
        } catch (Exception e) {
            return AuthenticationResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .message("Error. Try again!")
                    .build();
        }
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .message("Login successfully!")
                    .build();
        } catch (Exception e) {
            return AuthenticationResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .message("Incorrect. Please try again!")
                    .build();
        }
    }

    @Override
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        AuthenticationResponse authResponse = null;
        if (authHeader == null || !authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            return AuthenticationResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .build();
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                try {
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return authResponse;
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
