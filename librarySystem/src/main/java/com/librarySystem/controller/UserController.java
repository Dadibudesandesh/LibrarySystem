package com.librarySystem.controller;
import ch.qos.logback.core.model.Model;
import com.librarySystem.entity.User;
import com.librarySystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("/saveUser")
    public String addUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {

        User userExist = userService.getUserByEmail(user.getEmail());

        if (userExist != null) {
            redirectAttributes.addFlashAttribute("error", "User of this email is Already Exists");
            return "redirect:/register";
        }
        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "Successfully Registered ... ! Wait For Approval");
        return "redirect:/register";
    }


}


