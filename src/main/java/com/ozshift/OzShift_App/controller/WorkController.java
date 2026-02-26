package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.entity.WorkRecord;
import com.ozshift.OzShift_App.service.WorkRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WorkController {

    private final WorkRecordService workRecordService;

    @PostMapping("/work/start")
    @ResponseBody
    public ResponseEntity<String> startWork(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam Long workspaceId,
                                            @RequestParam Double latitude,
                                            @RequestParam Double longitude) {
        if (userDetails != null) {
            workRecordService.startWork(userDetails.getUsername(), workspaceId, latitude, longitude);
            return ResponseEntity.ok("Work started");
        }
        return ResponseEntity.badRequest().body("User not authenticated");
    }

    @PostMapping("/work/finish")
    @ResponseBody
    public ResponseEntity<String> finishWork(@RequestParam Long recordId,
                                             @RequestParam Double latitude,
                                             @RequestParam Double longitude) {
        workRecordService.finishWork(recordId, latitude, longitude);
        return ResponseEntity.ok("Work finished");
    }

    @PostMapping("/work/break/start")
    @ResponseBody
    public ResponseEntity<String> startBreak(@RequestParam Long recordId) {
        workRecordService.startBreak(recordId);
        return ResponseEntity.ok("Break started");
    }

    @PostMapping("/work/break/finish")
    @ResponseBody
    public ResponseEntity<String> finishBreak(@RequestParam Long recordId) {
        workRecordService.finishBreak(recordId);
        return ResponseEntity.ok("Break finished");
    }

    @GetMapping("/work/manage/{workspaceId}")
    public String manageWork(@PathVariable Long workspaceId, Model model) {
        List<WorkRecord> records = workRecordService.getRecordsByWorkspace(workspaceId);
        model.addAttribute("records", records);
        model.addAttribute("workspaceId", workspaceId);
        return "work_manage";
    }

    @PostMapping("/work/approve/{recordId}")
    @ResponseBody
    public ResponseEntity<String> approveWork(@PathVariable Long recordId) {
        workRecordService.approveRecord(recordId);
        return ResponseEntity.ok("Approved");
    }

    @PostMapping("/work/reject/{recordId}")
    @ResponseBody
    public ResponseEntity<String> rejectWork(@PathVariable Long recordId) {
        workRecordService.rejectRecord(recordId);
        return ResponseEntity.ok("Rejected");
    }
}
