package com.example.penguinproject.service;

import com.example.penguinproject.dto.UserDto;
import com.example.penguinproject.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    public ResponseEntity<List<UserDto>> getAllUsers(String email);

    public ResponseEntity<UserDto> getUserById(Integer id);

    public ResponseEntity<UserDto> editUser(Integer id, UserDto userDto);

    public ResponseEntity<String> deleteUser(Integer id);

}
