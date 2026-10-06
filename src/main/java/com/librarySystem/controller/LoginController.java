package com.librarySystem.controller;

import com.librarySystem.entity.User;
import com.librarySystem.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String loginPage(@RequestParam String email,
                            @RequestParam String password,
                            Model model,
                            HttpSession session) {

        User user = loginService.login(email, password);

//        if (email.equals("admin@gmail.com") && password.equals("admin")) {
//            session.setAttribute("loggedInUser", null);
//            session.setAttribute("role", "admin");
//            return "redirect:/admin/home";
//        }
//
//        if (user != null) {
//            session.setAttribute("loggedInUser", user);
//            session.setAttribute("role", "student");
//            return "redirect:/student/home";
//        }

        if (user == null) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        if (!"APPROVED".equals(user.getStatus())) {
            model.addAttribute("error", "Your account is not approved yet");
            return "login";
        }

        session.setAttribute("loggedInUser", user);
        session.setAttribute("role", user.getRole());

        if ("admin".equals(user.getRole())) {
            return "redirect:/admin/home";
        } else if ("student".equals(user.getRole())) {
            return "redirect:/student/home";
        }

        return "login";
    }

    @GetMapping("/current-user")
    public String currentUser(HttpSession session, Model model) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            model.addAttribute("message", "No user is logged in.");
            return "error";
        }
        model.addAttribute("user", loggedInUser);
        return "userDetails";
    }

    @PostMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
