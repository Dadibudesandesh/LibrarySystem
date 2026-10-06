package com.librarySystem.service;

import com.librarySystem.entity.User;
import com.librarySystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepositoryRepo;

    public void save(User s) {
        userRepositoryRepo.save(s);
    }

    public List<User> getAllStudent(String role) {
        return userRepositoryRepo.findByRole(role);
    }

    public void deleteById(long id) {
        userRepositoryRepo.deleteById(id);
    }

    public Object getStudentById(long id) {
        return userRepositoryRepo.findById(id);
    }


    public User getUserByEmail(String email) {
        return userRepositoryRepo.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepositoryRepo.findAll();
    }

    public List<User> findByStatus(String pending) {
        return userRepositoryRepo.findByStatus(pending);
    }

    public User getUserById(Long id) {
        return userRepositoryRepo.findById(id).orElse(null);
    }
}

