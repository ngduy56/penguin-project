package com.example.penguinproject.controller;

import com.example.penguinproject.common.ApiResponse;
import com.example.penguinproject.dto.UserDto;
import com.example.penguinproject.model.User;
import com.example.penguinproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/hello")
    public ResponseEntity<String> getHello() {
        return new ResponseEntity<String>("Xin chào các bạn!", HttpStatus.OK);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(@RequestParam(required = false) String email) {
        return userService.getAllUsers(email);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> editUser(@PathVariable("id") Integer id, @RequestBody UserDto userDto) {
        return userService.editUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Integer id) {
        return userService.deleteUser(id);
    }
}
