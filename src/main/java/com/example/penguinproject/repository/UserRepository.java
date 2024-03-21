package com.example.penguinproject.repository;

import com.example.penguinproject.dto.UserDto;
import com.example.penguinproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT new com.example.penguinproject.dto.UserDto(a.id, a.username, a.email, a.gender)  FROM User a where a.email like %:email%")
    List<UserDto> getAllUsers(@RequestParam(value = "email", defaultValue = "", required = false) String email);


    @Query("SELECT new com.example.penguinproject.dto.UserDto(a.id, a.username, a.email, a.gender)  FROM User a where a.id = :id")
    UserDto getUserById(@PathVariable(value = "id", required = false) Integer id);

    Optional<User> findByEmail(String email);

}
