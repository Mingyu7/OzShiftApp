package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.WorkRecord;
import com.ozshift.OzShift_App.service.UserService;
import com.ozshift.OzShift_App.service.WorkRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WorkRecordService workRecordService;

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

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userService.getUserByEmail(userDetails.getUsername());
            model.addAttribute("user", user);
            
            // 최근 근무 기록 5개만 가져오기 (샘플)
            List<WorkRecord> records = workRecordService.getMyWorkRecords(userDetails.getUsername());
            model.addAttribute("recentRecords", records.stream().limit(5).toList());
            
            return "mypage";
        }
        return "redirect:/login";
    }

    @GetMapping("/mypage/history")
    public String myHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userService.getUserByEmail(userDetails.getUsername());
            model.addAttribute("user", user);
            
            List<WorkRecord> records = workRecordService.getMyWorkRecords(userDetails.getUsername());
            model.addAttribute("records", records);
            
            return "mypage_history";
        }
        return "redirect:/login";
    }
}