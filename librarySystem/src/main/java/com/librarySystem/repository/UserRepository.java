package com.librarySystem.repository;

import com.librarySystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByName(String name);

    List<User> findByRole(String role);

    List<User> findByStatus(String pending);


    long countByStatus(String approve);
}
