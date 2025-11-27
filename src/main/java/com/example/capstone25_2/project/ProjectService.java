package com.example.capstone25_2.project;

import com.example.capstone25_2.memo.MemoRepository;
import com.example.capstone25_2.project.dto.ProjectMemberDto;
import com.example.capstone25_2.task.TaskRepository;
import com.example.capstone25_2.user.UserRepository;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectResponse;
import com.example.capstone25_2.project.dto.UpdateProjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository; // 프로젝트 삭제 시 필요
    private final MemoRepository memoRepository; // 프로젝트 삭제 시 필요

    // Helper: 로그인 ID(String)를 User PK(Long)로 변환
    public Long getUserPkId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        return user.getPk_id();
    }

    // Helper: User PK(Long)를 User Login ID(String)로 변환
    private String getLoginIdByUserPk(Long userPkId) {
        User user = userRepository.findById(userPkId)
                .orElseThrow(() -> new IllegalStateException("프로젝트 소유자 정보를 찾을 수 없습니다."));
        return user.getId();
    }

    // [1] 프로젝트 목록 조회
    public List<ProjectResponse> findProjectsByUserId(String userId) {
        List<Project> projects = projectRepository.findDistinctByMembers_UserId(userId);

        return projects.stream()
                .map(project -> {
                    String ownerLoginId = getLoginIdByUserPk(project.getUsersId());
                    List<String> memberIds = project.getMembers().stream()
                            .map(ProjectMember::getUserId)
                            .collect(Collectors.toList());

                    List<String> sortedMembers = new ArrayList<>();
                    if (memberIds.contains(ownerLoginId)) {
                        sortedMembers.add(ownerLoginId);
                    }
                    memberIds.stream()
                            .filter(id -> !id.equals(ownerLoginId))
                            .sorted()
                            .forEach(sortedMembers::add);

                    String allMembers = String.join(", ", sortedMembers);
                    return new ProjectResponse(project, ownerLoginId, allMembers);
                })
                .collect(Collectors.toList());
    }

    // [2] 접근 권한 확인 (단순 페이지 접근용)
    public boolean checkUserAccessById(String userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));

        if (isMember) return true;

        throw new SecurityException("해당 프로젝트에 접근 권한이 없습니다.");
    }

    // ⭐️ [3] 쓰기/수정 권한 검증 메서드 (Task, Memo 등 공용) ⭐️
    public void validateWriteAccess(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 1. 소유자(Owner)라면 통과
        Long userPk = getUserPkId(userId);
        if (project.getUsersId().equals(userPk)) {
            return;
        }

        // 2. 멤버 역할 확인
        ProjectRole role = project.getMemberRole(userId)
                .orElseThrow(() -> new SecurityException("프로젝트 멤버가 아닙니다."));

        // 3. VIEWER라면 차단
        if (role == ProjectRole.VIEWER) {
            throw new SecurityException("읽기 전용 권한입니다. (수정 불가)");
        }
    }

    // ⭐️ [4] 현재 유저의 역할 반환 (Controller용 헬퍼) ⭐️
    public ProjectRole getUserRoleInProject(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 없음"));

        // 소유자인지 확인 -> 소유자라면 MEMBER 권한 이상으로 간주 (여기서는 MEMBER 리턴)
        Long userPk = getUserPkId(userId);
        if (project.getUsersId().equals(userPk)) {
            return ProjectRole.MEMBER;
        }

        return project.getMemberRole(userId).orElse(ProjectRole.VIEWER);
    }

    // [5] 프로젝트 생성
    @Transactional
    public Project save(String creatorId, AddProjectRequest request) {
        Long creatorPkId = getUserPkId(creatorId);
        Project newProject = Project.from(request);

        // 소유자 설정 및 멤버 추가 (기본 역할 MEMBER)
        newProject.setUsersId(creatorPkId);
        newProject.addMember(creatorId, ProjectRole.MEMBER);

        return projectRepository.save(newProject);
    }

    // [6] 프로젝트 탈퇴
    @Transactional
    public void leaveProject(String userId, Long projectId) {
        Long userPkId = getUserPkId(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("탈퇴할 프로젝트를 찾을 수 없습니다."));

        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));

        if (!isMember) throw new IllegalArgumentException("해당 프로젝트의 멤버가 아닙니다.");

        if (project.getUsersId() != null && project.getUsersId().equals(userPkId)) {
            throw new IllegalArgumentException("프로젝트 소유자는 탈퇴할 수 없습니다.");
        }
        project.removeMember(userId);
    }

    // (사용 안함 - deleteProject 사용 권장)
    @Transactional
    public void delete(long id) {
        projectRepository.deleteById(id);
    }

    // [7] 프로젝트 수정
    @Transactional
    public void updateProject(Long projectId, String requesterId, UpdateProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 소유자 권한 체크
        Long requesterPk = getUserPkId(requesterId);
        if (!project.getUsersId().equals(requesterPk)) {
            throw new SecurityException("프로젝트 소유자만 설정을 변경할 수 있습니다.");
        }

        project.update(request);
    }

    // (기존 update 메서드 오버로딩 - 필요 시 유지)
    @Transactional
    public Project update(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: " + id));
        project.update(request);
        return project;
    }

    @Transactional(readOnly = true)
    public String getProjectNameById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return project.getProjectName();
    }

    // [8] 멤버 목록 조회 (DTO 반환)
    @Transactional(readOnly = true)
    public List<ProjectMemberDto> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<ProjectMemberDto> dtoList = new ArrayList<>();
        for (ProjectMember pm : project.getMembers()) {
            User user = userRepository.findById(pm.getUserId()).orElse(null);
            if (user != null) {
                Long ownerPk = project.getUsersId();
                boolean isOwner = ownerPk != null && ownerPk.equals(user.getPk_id());
                dtoList.add(new ProjectMemberDto(user, pm.getRole(), isOwner));
            }
        }
        return dtoList;
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
    }

    // [9] 멤버 초대
    @Transactional
    public void inviteMember(Long projectId, String inviterId, String emailOrId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Long inviterPk = getUserPkId(inviterId);
        if (!project.getUsersId().equals(inviterPk)) {
            throw new SecurityException("프로젝트 소유자만 멤버를 초대할 수 있습니다.");
        }

        User userToInvite = userRepository.findById(emailOrId)
                .orElseGet(() -> userRepository.findByEmail(emailOrId)
                        .orElseThrow(() -> new IllegalArgumentException("'" + emailOrId + "' 사용자를 찾을 수 없습니다.")));

        boolean isAlreadyMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userToInvite.getId()));

        if (isAlreadyMember) {
            throw new IllegalArgumentException("이미 참여 중인 멤버입니다.");
        }

        project.addMember(userToInvite.getId(), ProjectRole.MEMBER);
    }

    // 멤버 추방
    @Transactional
    public void kickMember(Long projectId, String requesterId, String targetMemberId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Long requesterPk = getUserPkId(requesterId);

        // 검증: 요청자가 소유자인가?
        if (!project.getUsersId().equals(requesterPk)) {
            throw new SecurityException("프로젝트 소유자만 멤버를 추방할 수 있습니다.");
        }

        project.removeMember(targetMemberId);
    }

    @Transactional(readOnly = true)
    public boolean isUserInProject(String userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));
    }

    // [10] 멤버 역할 변경
    @Transactional
    public void updateMemberRole(Long projectId, String ownerId, String targetUserId, ProjectRole newRole) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Long ownerPk = getUserPkId(ownerId);

        if (!project.getUsersId().equals(ownerPk)) {
            throw new SecurityException("소유자만 역할을 변경할 수 있습니다.");
        }

        project.getMembers().stream()
                .filter(m -> m.getUserId().equals(targetUserId))
                .findFirst()
                .ifPresent(member -> member.setRole(newRole));
    }

    // ⭐️ [11] 프로젝트 삭제 (소유자 전용) ⭐️
    @Transactional
    public void deleteProject(Long projectId, String userId, String password) {
        // 1. 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 2. 사용자(요청자) 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 소유자 권한 확인
        if (!project.getUsersId().equals(user.getPk_id())) {
            throw new SecurityException("프로젝트 소유자만 삭제할 수 있습니다.");
        }

        // 4. ⭐️ 비밀번호 검증 ⭐️
        // (만약 PasswordEncoder를 사용 중이라면 passwordEncoder.matches(password, user.getPassword()) 사용)
        if (!user.getPassword().equals(password)) {
            throw new SecurityException("비밀번호가 일치하지 않습니다.");
        }

        // 5. 연관된 데이터 삭제 (메모, 업무)
        memoRepository.deleteByProjectId(projectId);
        taskRepository.deleteByProjectId(projectId);

        // 6. 프로젝트 삭제
        projectRepository.delete(project);
    }
}