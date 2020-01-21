package com.seliverstov.movier.repository;

import com.seliverstov.movier.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    List<User> findAll();

    User findByMail(String mail);
    User findByUsername(String username);

    User findByActivationCode(String code);
}