package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.entity.Member;
import com.ozshift.OzShift_App.entity.Workspace;
import com.ozshift.OzShift_App.service.ShiftService;
import com.ozshift.OzShift_App.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ozshift.OzShift_App.entity.Shift;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/workspace/{workspaceId}/shift")
public class ShiftController {

    private final ShiftService shiftService;
    private final WorkspaceService workspaceService;

    @GetMapping("/list")
    public String shiftList(@PathVariable Long workspaceId, Model model) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        List<Shift> shifts = shiftService.getWorkspaceShifts(workspaceId, null); // null for memberId to get all shifts

        model.addAttribute("workspace", workspace);
        model.addAttribute("shifts", shifts);
        return "shift_list";
    }

    @GetMapping("/add")
    public String addShiftForm(@PathVariable Long workspaceId, Model model) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        List<Member> members = workspaceService.getMembersByWorkspaceId(workspaceId);
        
        model.addAttribute("workspace", workspace);
        model.addAttribute("members", members);
        return "shift_add";
    }

    @PostMapping("/add")
    public String addShift(@PathVariable Long workspaceId,
                           @RequestParam Long memberId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                           @RequestParam Double hourlyRate) {
        shiftService.addShift(workspaceId, memberId, startTime, endTime, hourlyRate);
        return "redirect:/";
    }

    @GetMapping("/edit/{shiftId}")
    public String editShiftForm(@PathVariable Long workspaceId, @PathVariable Long shiftId, Model model) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        List<Member> members = workspaceService.getMembersByWorkspaceId(workspaceId);
        Shift shift = shiftService.getShiftById(shiftId);

        model.addAttribute("workspace", workspace);
        model.addAttribute("members", members);
        model.addAttribute("shift", shift);
        return "shift_edit";
    }

    @PostMapping("/update/{shiftId}")
    public String updateShift(@PathVariable Long workspaceId,
                              @PathVariable Long shiftId,
                              @RequestParam Long memberId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                              @RequestParam Double hourlyRate) {
        shiftService.updateShift(shiftId, memberId, startTime, endTime, hourlyRate);
        return "redirect:/workspace/" + workspaceId + "/shift/list";
    }

    @PostMapping("/delete/{shiftId}")
    public String deleteShift(@PathVariable Long workspaceId, @PathVariable Long shiftId) {
        shiftService.deleteShift(shiftId);
        return "redirect:/workspace/" + workspaceId + "/shift/list";
    }
}
