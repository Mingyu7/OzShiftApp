package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.Shift;
import com.ozshift.OzShift_App.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ozshift.OzShift_App.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByUserOrderByStartTimeDesc(User user); // 기존 유지
    Page<Shift> findByUserOrderByStartTimeDesc(User user, Pageable pageable); // 페이징 추가
    
    List<Shift> findByUserAndWorkspaceOrderByStartTimeDesc(User user, Workspace workspace); // 기존 유지
    Page<Shift> findByUserAndWorkspaceOrderByStartTimeDesc(User user, Workspace workspace, Pageable pageable); // 페이징 추가
    
    // 매니저 조회용 (시간 순서대로 = 오름차순 Asc)
    List<Shift> findByWorkspaceOrderByStartTimeAsc(Workspace workspace);
    List<Shift> findByUserAndWorkspaceOrderByStartTimeAsc(User user, Workspace workspace);

    @Query("SELECT s FROM Shift s WHERE s.user.id = :userId AND s.workspace = :workspace ORDER BY s.startTime ASC")
    List<Shift> findByUserIdAndWorkspaceOrderByStartTimeAsc(@Param("userId") Long userId, @Param("workspace") Workspace workspace);

    List<Shift> findByUser(User user);
}