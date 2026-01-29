package com.ozshift.OzShift_App.service;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.entity.Workspace;
import com.ozshift.OzShift_App.repository.UserRepository;
import com.ozshift.OzShift_App.repository.WorkspaceRepository;
import com.ozshift.OzShift_App.entity.Member;
import com.ozshift.OzShift_App.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

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


// ...

    public List<Workspace> getMyWorkspaces(String email) {
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return workspaceRepository.findAllByManager(manager);
    }

    public Page<Workspace> getMyWorkspaces(String email, Pageable pageable) {
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return workspaceRepository.findAllByManager(manager, pageable);
    }

    public Workspace getWorkspaceById(Long id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid workspace ID"));
    }

    public List<Member> getMembersByWorkspaceId(Long workspaceId) {
        Workspace workspace = getWorkspaceById(workspaceId);
        return memberRepository.findByWorkspace(workspace);
    }

    @Transactional(readOnly = true)
    public List<Workspace> getWorkspacesByUser(User user) {
        return memberRepository.findWorkspacesByUser(user);
    }

    @Transactional
    public void joinWorkspace(String uuid, String userEmail) {
        Workspace workspace = workspaceRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 초대 코드입니다."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 참여 중인지 확인 (간단한 로직)
        boolean isAlreadyMember = memberRepository.findByWorkspace(workspace).stream()
                .anyMatch(m -> m.getUser().getId().equals(user.getId()));

        if (isAlreadyMember) {
            throw new IllegalStateException("이미 해당 워크스페이스에 참여 중입니다.");
        }

        Member member = new Member();
        member.setWorkspace(workspace);
        member.setUser(user);

        memberRepository.save(member);
    }
}