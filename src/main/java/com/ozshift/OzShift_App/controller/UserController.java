package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(User user) {
        userService.register(user);
        return "redirect:/login"; // 가입 후 로그인 페이지로 이동
    }
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // templates/login.html 을 찾아감
    }
}