package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findByUuid(String uuid); // UUID로 방 찾기
    List<Workspace> findAllByManager(User manager); // 전체 목록 조회 (매니저) - 기존 유지 (혹시 다른곳에서 쓸까봐)
    Page<Workspace> findAllByManager(User manager, Pageable pageable); // 페이징 적용된 목록 조회
}