package com.example.penguinproject.repository;

import java.util.List;
import java.util.Optional;

import com.example.penguinproject.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("select t from Token t join t.user u where u.id = :id and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByUser(Integer id);

    Optional<Token> findByToken(String token);
}
