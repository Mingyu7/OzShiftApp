package com.ozshift.OzShift_App.service;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.Workspace;
import com.ozshift.OzShift_App.repository.UserRepository;
import com.ozshift.OzShift_App.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createWorkspace(String name, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setManager(manager);
        // UUID는 Entity에서 자동 생성됨
        workspaceRepository.save(workspace);
    }
}