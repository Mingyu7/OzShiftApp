package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.dto.ShiftCalendarEventDTO;
import com.ozshift.OzShift_App.entity.Shift;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final ShiftService shiftService;
    private final UserService userService;

    @GetMapping("/{workspaceId}")
    public String workspaceCalendar(@PathVariable("workspaceId") Long workspaceId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.getUserByEmail(userDetails.getUsername());
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);

        // 사용자가 해당 워크스페이스의 멤버인지 확인하는 로직 (필요 시 추가)
        // 예: if (!workspaceService.isUserMemberOfWorkspace(user, workspaceId)) { ... }

        model.addAttribute("workspace", workspace);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("realName", user.getName());

        return "workspace_calendar";
    }

    @GetMapping("/{workspaceId}/events")
    @ResponseBody
    public List<ShiftCalendarEventDTO> getShiftEvents(@PathVariable("workspaceId") Long workspaceId,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return new ArrayList<>();
        }

        User user = userService.getUserByEmail(userDetails.getUsername());
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);

        List<Shift> shifts = shiftService.getShiftsByUserAndWorkspace(user, workspace);
        List<ShiftCalendarEventDTO> shiftEvents = new ArrayList<>();
        for (Shift shift : shifts) {
            long minutes = Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes();
            double hours = minutes / 60.0;
            double pay = hours * shift.getHourlyRate();
            shiftEvents.add(new ShiftCalendarEventDTO("근무", shift.getStartTime(), shift.getEndTime(), shift.getHourlyRate(), pay));
        }

        return shiftEvents;
    }
}
