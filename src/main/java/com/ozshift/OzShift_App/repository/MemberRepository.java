package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.Member;
import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUser(User user); // 사용자가 속한 방 목록 찾기
    List<Member> findByWorkspace(Workspace workspace); // 워크스페이스에 속한 멤버 목록 찾기

    @Query("SELECT m.workspace FROM Member m WHERE m.user = :user")
    List<Workspace> findWorkspacesByUser(@Param("user") User user);
}