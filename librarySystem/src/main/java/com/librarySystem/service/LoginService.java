package com.librarySystem.service;

import com.librarySystem.entity.User;
import com.librarySystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }
        if(user.getPassword().equals(password)) {
            System.out.println("Password matches!");
            return user;
        }
        return null;
    }
}
