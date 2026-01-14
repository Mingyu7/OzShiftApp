package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.Shift;
import com.ozshift.OzShift_App.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByUser(User user); // 특정 사용자의 스케줄 찾기
}