package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.WorkRecord;
import com.ozshift.OzShift_App.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkRecordRepository extends JpaRepository<WorkRecord, Long> {
    List<WorkRecord> findByWorkspace(Workspace workspace);
    List<WorkRecord> findByUserOrderByStartTimeDesc(User user);
    List<WorkRecord> findByStatusAndEndTimeIsNotNull(WorkRecord.ApprovalStatus status);
}
