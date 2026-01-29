package com.ozshift.OzShift_App.service;

import com.ozshift.OzShift_App.entity.Member;
import com.ozshift.OzShift_App.entity.Shift;
import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.Workspace;
import com.ozshift.OzShift_App.repository.MemberRepository;
import com.ozshift.OzShift_App.repository.ShiftRepository;
import com.ozshift.OzShift_App.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addShift(Long workspaceId, Long memberId, LocalDateTime startTime, LocalDateTime endTime, Double hourlyRate) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid workspace ID"));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        
        if (!member.getWorkspace().getId().equals(workspaceId)) {
             throw new IllegalArgumentException("Member does not belong to this workspace");
        }

        Shift shift = new Shift();
        shift.setWorkspace(workspace);
        shift.setUser(member.getUser());
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setHourlyRate(hourlyRate);

        shiftRepository.save(shift);
    }



// ...

    public Page<Shift> getShiftsByUser(User user, Long workspaceId, Pageable pageable) {
        if (workspaceId != null) {
            Workspace workspace = workspaceRepository.findById(workspaceId)
                    .orElse(null);
            if (workspace != null) {
                return shiftRepository.findByUserAndWorkspaceOrderByStartTimeDesc(user, workspace, pageable);
            }
        }
        return shiftRepository.findByUserOrderByStartTimeDesc(user, pageable);
    }

    public List<Shift> getWorkspaceShifts(Long workspaceId, Long memberId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid workspace ID"));

        if (memberId != null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
            return shiftRepository.findByUserAndWorkspaceOrderByStartTimeAsc(member.getUser(), workspace);
        }
        
        return shiftRepository.findByWorkspaceOrderByStartTimeAsc(workspace);
    }

    public Shift getShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid shift ID"));
    }

    @Transactional
    public void updateShift(Long shiftId, Long memberId, LocalDateTime startTime, LocalDateTime endTime, Double hourlyRate) {
        Shift shift = getShiftById(shiftId);
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 유저가 변경되었을 경우 업데이트
        if (!shift.getUser().getId().equals(member.getUser().getId())) {
             shift.setUser(member.getUser());
        }
        
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setHourlyRate(hourlyRate);
    }

    @Transactional
    public void deleteShift(Long shiftId) {
        shiftRepository.deleteById(shiftId);
    }
}
