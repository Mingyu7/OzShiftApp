package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findByUuid(String uuid); // UUID로 방 찾기
}