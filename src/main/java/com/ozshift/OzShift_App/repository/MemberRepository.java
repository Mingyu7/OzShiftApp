package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.Member;
import com.ozshift.OzShift_App.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUser(User user); // 사용자가 속한 방 목록 찾기
}