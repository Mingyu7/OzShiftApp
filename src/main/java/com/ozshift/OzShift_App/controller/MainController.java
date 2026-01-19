package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final WorkspaceService workspaceService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        // 사용자의 권한(ROLE)에 따라 화면을 다르게 보여주기 위해 전달
        model.addAttribute("role", userDetails.getAuthorities().toString());
        return "index";
    }

    @PostMapping("/workspace/create")
    public String createWorkspace(String name, @AuthenticationPrincipal UserDetails userDetails) {
        workspaceService.createWorkspace(name, userDetails.getUsername());
        return "redirect:/";
    }
}