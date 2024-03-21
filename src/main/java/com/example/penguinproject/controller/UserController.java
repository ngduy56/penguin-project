package com.example.penguinproject.controller;

import com.example.penguinproject.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/hello")
    public ResponseEntity<String> getHello() {
        User user = new User();
        return new ResponseEntity<>("Xin chào mày nhé!", HttpStatus.OK);
    }
}
