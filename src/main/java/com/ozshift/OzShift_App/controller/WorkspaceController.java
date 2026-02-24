package com.ozshift.OzShift_App.controller;

import com.ozshift.OzShift_App.dto.ShiftCalendarEventDTO;
import com.ozshift.OzShift_App.entity.Member;
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
import org.springframework.web.bind.annotation.RequestParam;
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
            shiftEvents.add(new ShiftCalendarEventDTO("근무", shift.getStartTime(), shift.getEndTime(), shift.getHourlyRate(), pay, user.getName()));
        }

        return shiftEvents;
    }

    @GetMapping("/{workspaceId}/admin")
    public String workspaceAdminCalendar(@PathVariable("workspaceId") Long workspaceId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.getUserByEmail(userDetails.getUsername());
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);

        // 매니저인지 확인
        if (workspace.getManager() == null || !workspace.getManager().getId().equals(user.getId())) {
             return "redirect:/";
        }

        model.addAttribute("workspace", workspace);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("realName", user.getName());

        return "workspace_admin_calendar";
    }

    @GetMapping("/{workspaceId}/admin/board")
    public String workspaceMemberBoard(@PathVariable("workspaceId") Long workspaceId,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
                                       Model model) {
        if (userDetails == null) return "redirect:/login";

        User user = userService.getUserByEmail(userDetails.getUsername());
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);

        if (workspace.getManager() == null || !workspace.getManager().getId().equals(user.getId())) {
            return "redirect:/";
        }

        List<Member> members = workspaceService.getMembersByWorkspaceId(workspaceId);
        List<Shift> shifts = shiftService.getWorkspaceShifts(workspaceId, null);

        java.time.LocalDate today = java.time.LocalDate.now().plusWeeks(weekOffset);
        java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        List<java.time.LocalDate> weekDates = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(startOfWeek.plusDays(i));
        }

        model.addAttribute("workspace", workspace);
        model.addAttribute("members", members);
        model.addAttribute("shifts", shifts);
        model.addAttribute("weekDates", weekDates);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("realName", user.getName());

        return "workspace_admin_member_board";
    }

    @GetMapping("/{workspaceId}/admin/events")
    @ResponseBody
    public List<ShiftCalendarEventDTO> getAllShiftEvents(@PathVariable("workspaceId") Long workspaceId,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return new ArrayList<>();
        }

        User user = userService.getUserByEmail(userDetails.getUsername());
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);

        // 매니저인지 확인
        if (workspace.getManager() == null || !workspace.getManager().getId().equals(user.getId())) {
             return new ArrayList<>();
        }

        List<Shift> shifts = shiftService.getWorkspaceShifts(workspaceId, null);
        List<ShiftCalendarEventDTO> shiftEvents = new ArrayList<>();
        for (Shift shift : shifts) {
            long minutes = Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes();
            double hours = minutes / 60.0;
            double pay = hours * shift.getHourlyRate();
            String title = shift.getUser().getName() + " - 근무";
            shiftEvents.add(new ShiftCalendarEventDTO(title, shift.getStartTime(), shift.getEndTime(), shift.getHourlyRate(), pay, shift.getUser().getName()));
        }

        return shiftEvents;
    }
}
