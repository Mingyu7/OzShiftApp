package com.ozshift.OzShift_App.service;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.Workspace;
import com.ozshift.OzShift_App.entity.WorkRecord;
import com.ozshift.OzShift_App.repository.UserRepository;
import com.ozshift.OzShift_App.repository.WorkspaceRepository;
import com.ozshift.OzShift_App.repository.WorkRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkRecordService {

    private final WorkRecordRepository workRecordRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 (00:00:00)에 실행
    public void autoApproveRecordsAtMidnight() {
        List<WorkRecord> pendingRecords = workRecordRepository.findByStatusAndEndTimeIsNotNull(WorkRecord.ApprovalStatus.PENDING);
        for (WorkRecord record : pendingRecords) {
            record.setStatus(WorkRecord.ApprovalStatus.APPROVED);
        }
        workRecordRepository.saveAll(pendingRecords);
        System.out.println("DEBUG: Auto-approved " + pendingRecords.size() + " work records at midnight.");
    }

    @Transactional
    public void startWork(String username, Long workspaceId, Double latitude, Double longitude) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));

        WorkRecord workRecord = new WorkRecord();
        workRecord.setUser(user);
        workRecord.setWorkspace(workspace);
        workRecord.setStartTime(LocalDateTime.now());
        workRecord.setStartLatitude(latitude);
        workRecord.setStartLongitude(longitude);
        workRecord.setStatus(WorkRecord.ApprovalStatus.PENDING);

        workRecordRepository.save(workRecord);
    }

    @Transactional
    public void finishWork(Long workRecordId, Double latitude, Double longitude) {
        WorkRecord workRecord = workRecordRepository.findById(workRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Work record not found"));

        if (workRecord.getBreakStartTime() != null) {
            finishBreak(workRecordId);
        }

        workRecord.setEndTime(LocalDateTime.now());
        workRecord.setEndLatitude(latitude);
        workRecord.setEndLongitude(longitude);
        workRecordRepository.save(workRecord);
    }

    @Transactional
    public void startBreak(Long workRecordId) {
        WorkRecord workRecord = workRecordRepository.findById(workRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Work record not found"));
        if (workRecord.getBreakStartTime() != null) {
            throw new IllegalStateException("Already on a break");
        }
        workRecord.setBreakStartTime(LocalDateTime.now());
        workRecordRepository.save(workRecord);
    }

    @Transactional
    public void finishBreak(Long workRecordId) {
        WorkRecord workRecord = workRecordRepository.findById(workRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Work record not found"));
        if (workRecord.getBreakStartTime() == null) {
            throw new IllegalStateException("Not on a break");
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(workRecord.getBreakStartTime(), now).toMinutes();

        workRecord.setTotalBreakMinutes(workRecord.getTotalBreakMinutes() + (int) minutes);
        workRecord.setBreakStartTime(null);
        workRecordRepository.save(workRecord);
    }

    public List<WorkRecord> getPendingRecords(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        return workRecordRepository.findByWorkspace(workspace).stream()
                .filter(r -> r.getStatus() == WorkRecord.ApprovalStatus.PENDING)
                .collect(Collectors.toList());
    }

    public WorkRecord getActiveRecord(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return workRecordRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()) && r.getEndTime() == null)
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void approveRecord(Long recordId) {
        WorkRecord record = workRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));
        record.setStatus(WorkRecord.ApprovalStatus.APPROVED);
        workRecordRepository.save(record);
    }

    @Transactional
    public void rejectRecord(Long recordId) {
        WorkRecord record = workRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));
        record.setStatus(WorkRecord.ApprovalStatus.REJECTED);
        workRecordRepository.save(record);
    }

    public List<WorkRecord> getRecordsByWorkspace(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        return workRecordRepository.findByWorkspace(workspace);
    }

    public List<WorkRecord> getMyWorkRecords(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return workRecordRepository.findByUserOrderByStartTimeDesc(user);
    }
}
