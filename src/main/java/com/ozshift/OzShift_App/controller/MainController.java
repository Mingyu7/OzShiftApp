package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.Workspace;
import com.ozshift.OzShift_App.service.ShiftService;
import com.ozshift.OzShift_App.service.UserService;
import com.ozshift.OzShift_App.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final WorkspaceService workspaceService;
    private final ShiftService shiftService;
    private final UserService userService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, 
                        @RequestParam(required = false) Long workspaceId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "0") int shiftPage,
                        Model model) {
        if (userDetails != null) {
            User user = userService.getUserByEmail(userDetails.getUsername());
            
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("realName", user.getName()); // 실제 이름 추가
            model.addAttribute("role", userDetails.getAuthorities().toString());

            // 매니저인 경우 본인이 만든 방 목록을 가져옴 (페이징)
            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
                Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
                Page<Workspace> workspacePage = workspaceService.getMyWorkspaces(userDetails.getUsername(), pageable);
                
                model.addAttribute("workspaces", workspacePage.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", workspacePage.getTotalPages());
            } else {
                // 직원이 소속된 워크스페이스 목록 (필터링 드롭다운용)
                List<Workspace> joinedWorkspaces = workspaceService.getWorkspacesByUser(user);
                model.addAttribute("joinedWorkspaces", joinedWorkspaces);
                
                // 선택된 워크스페이스(또는 전체)의 스케줄 조회 (페이징)
                Pageable shiftPageable = PageRequest.of(shiftPage, 5, Sort.by("startTime").descending());
                Page<com.ozshift.OzShift_App.entity.Shift> shiftPageObj = shiftService.getShiftsByUser(user, workspaceId, shiftPageable);

                // 전체 예상 급여 계산
                List<com.ozshift.OzShift_App.entity.Shift> allShifts = shiftService.getAllShiftsByUser(user, workspaceId);
                double totalExpectedPay = 0.0;
                for (com.ozshift.OzShift_App.entity.Shift shift : allShifts) {
                    Duration duration = Duration.between(shift.getStartTime(), shift.getEndTime());
                    double hoursWorked = duration.toMinutes() / 60.0;
                    totalExpectedPay += hoursWorked * shift.getHourlyRate();
                }
                
                model.addAttribute("myShifts", shiftPageObj.getContent());
                model.addAttribute("shiftCurrentPage", shiftPage);
                model.addAttribute("shiftTotalPages", shiftPageObj.getTotalPages());
                model.addAttribute("selectedWorkspaceId", workspaceId);
                model.addAttribute("totalEarnings", user.getTotalEarnings() != null ? user.getTotalEarnings() : 0.0);
                model.addAttribute("totalExpectedPay", totalExpectedPay);
            }
        }
        return "index";
    }

    @PostMapping("/workspace/create")
    public String createWorkspace(String name, @AuthenticationPrincipal UserDetails userDetails) {
        workspaceService.createWorkspace(name, userDetails.getUsername());
        return "redirect:/";
    }

    @PostMapping("/workspace/join")
    public String joinWorkspace(String uuid, @AuthenticationPrincipal UserDetails userDetails) {
        workspaceService.joinWorkspace(uuid, userDetails.getUsername());
        return "redirect:/";
    }
}