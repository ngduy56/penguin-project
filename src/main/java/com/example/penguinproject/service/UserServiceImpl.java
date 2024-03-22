package com.example.penguinproject.service;


import com.example.penguinproject.common.ApiResponse;
import com.example.penguinproject.dto.UserDto;
import com.example.penguinproject.model.Token;
import com.example.penguinproject.model.User;
import com.example.penguinproject.repository.TokenRepository;
import com.example.penguinproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(String email) {
        List<UserDto> users = new ArrayList<>();
        ApiResponse<List<UserDto>> response = null;
        try {
            users = userRepository.getAllUsers(email);
            response = new ApiResponse<>(200, "Success", users);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ApiResponse<>(500, "Error", null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserDto> getUserById(Integer id) {
        Optional<UserDto> user = Optional.ofNullable(userRepository.getUserById(id));
        try {
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserDto> editUser(Integer id, UserDto userDto) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User _user = userData.get();
            _user.setUsername(userDto.getUsername());
            _user.setGender(userDto.getGender());
            _user.setEmail(userDto.getEmail());
            _user.setUpdateAt(new Date());

            User savedUser = userRepository.save(_user);
            UserDto savedUserDto = UserDto.fromUser(savedUser);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(Integer id) {
        List<Token> allToken = tokenRepository.findAllValidTokenByUser(id);
        for (Token tk : allToken) {
            tokenRepository.deleteAllById(Collections.singleton(tk.getId()));
        }
        userRepository.deleteById(id);
        return new ResponseEntity<>("Delete successfully!", HttpStatus.OK);
    }
}
