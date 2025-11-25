package com.example.capstone25_2.project;

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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Helper: 로그인 ID(String)를 User PK(Long)로 변환
    private Long getUserPkId(String userId) {
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

    // [MODIFIED] 프로젝트 목록 조회: ProjectMember 엔티티 조회로 변경
    public List<ProjectResponse> findProjectsByUserId(String userId) {
        // ProjectMember 엔티티를 통해 userId가 포함된 프로젝트 조회 (Repository 쿼리 변경 필요)
        // findDistinctByMembers_UserId(String userId) 사용
        List<Project> projects = projectRepository.findDistinctByMembers_UserId(userId);

        return projects.stream()
                .map(project -> {
                    // 1. 소유자 로그인 ID 조회
                    String ownerLoginId = getLoginIdByUserPk(project.getUsersId());

                    // 2. 모든 멤버 ID 리스트 추출 (ProjectMember -> userId)
                    List<String> memberIds = project.getMembers().stream()
                            .map(ProjectMember::getUserId)
                            .collect(Collectors.toList());

                    // 3. 정렬된 리스트 생성 (소유자 우선)
                    List<String> sortedMembers = new ArrayList<>();
                    if (memberIds.contains(ownerLoginId)) {
                        sortedMembers.add(ownerLoginId);
                    }

                    memberIds.stream()
                            .filter(id -> !id.equals(ownerLoginId))
                            .sorted()
                            .forEach(sortedMembers::add);

                    // 4. 문자열 변환
                    String allMembers = String.join(", ", sortedMembers);

                    // 5. DTO 생성
                    return new ProjectResponse(project, ownerLoginId, allMembers);
                })
                .collect(Collectors.toList());
    }

    //  [MODIFIED] 접근 권한 확인: ProjectMember 엔티티 검사
    public boolean checkUserAccessById(String userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 멤버 리스트에서 해당 userId가 있는지 확인
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));

        if (isMember) {
            return true;
        }

        throw new SecurityException("해당 프로젝트에 접근 권한이 없습니다.");
    }

    //  [MODIFIED] 프로젝트 생성: 소유자를 MEMBER 역할로 추가
    @Transactional
    public Project save(String creatorId, AddProjectRequest request) {
        Long creatorPkId = getUserPkId(creatorId);

        Project newProject = Project.from(request);

        // 1. 소유자 PK 설정 (최고 관리자)
        newProject.setUsersId(creatorPkId);

        // 2. 멤버 리스트에 생성자 추가 (기본 역할 MEMBER)
        // (소유자도 프로젝트 멤버로 등록되어야 조회 등에 편리함)
        newProject.addMember(creatorId, ProjectRole.MEMBER);

        return projectRepository.save(newProject);
    }

    // [MODIFIED] 프로젝트 탈퇴 로직
    @Transactional
    public void leaveProject(String userId, Long projectId) {
        Long userPkId = getUserPkId(userId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("탈퇴할 프로젝트를 찾을 수 없습니다."));

        // 멤버인지 확인
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));

        if (!isMember) {
            throw new IllegalArgumentException("해당 프로젝트의 멤버가 아닙니다.");
        }

        // 소유자는 탈퇴 불가
        if (project.getUsersId() != null && project.getUsersId().equals(userPkId)) {
            throw new IllegalArgumentException("프로젝트 소유자는 탈퇴할 수 없습니다.");
        }

        // 탈퇴 처리 (ProjectMember 엔티티 제거)
        project.removeMember(userId);
    }

    // [유지] 프로젝트 삭제
    @Transactional
    public void delete(long id) {
        projectRepository.deleteById(id);
    }

    // [유지] 프로젝트 수정 (권한 체크 추가 가능)
    @Transactional
    public Project update(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: " + id));

        // (선택) 여기서 수정 권한 체크 로직 추가 가능
        project.update(request);

        return project;
    }

    @Transactional(readOnly = true)
    public String getProjectNameById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return project.getProjectName();
    }

    // [MODIFIED] 프로젝트 멤버 조회: ProjectMember -> userId 추출
    @Transactional(readOnly = true)
    public List<User> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // ProjectMember 엔티티에서 userId(String) 목록 추출
        Set<String> memberLoginIds = project.getMembers().stream()
                .map(ProjectMember::getUserId)
                .collect(Collectors.toSet());

        if (memberLoginIds.isEmpty()) {
            return List.of();
        }

        // UserRepository에서 User 엔티티 리스트 조회
        return userRepository.findAllByIdIn(memberLoginIds);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
    }

    // [MODIFIED] 팀원 초대: 역할(MEMBER) 지정하여 추가
    @Transactional
    public void inviteMember(Long projectId, String inviterId, String emailOrId) { // inviterId 추가

        // 1. 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 2.  [권한 체크] 초대하는 사람이 '소유자'인지 확인
        Long inviterPk = getUserPkId(inviterId); // 로그인한 사람의 PK 조회

        // 프로젝트 소유자(usersId)와 초대자(inviterPk)가 다르면 예외 발생
        if (!project.getUsersId().equals(inviterPk)) {
            throw new SecurityException("프로젝트 소유자만 멤버를 초대할 수 있습니다.");
        }

        // 3. 초대할 사용자 조회
        User userToInvite = userRepository.findById(emailOrId)
                .orElseGet(() -> userRepository.findByEmail(emailOrId)
                        .orElseThrow(() -> new IllegalArgumentException("'" + emailOrId + "' 사용자를 찾을 수 없습니다.")));

        // 4. 이미 참여 중인지 확인 (기존 로직 수정: ProjectMember 리스트 확인)
        boolean isAlreadyMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userToInvite.getId()));

        if (isAlreadyMember) {
            throw new IllegalArgumentException("이미 참여 중인 멤버입니다.");
        }

        // 5. 멤버 추가 (기본 역할 MEMBER)
        project.addMember(userToInvite.getId(), ProjectRole.MEMBER);
    }

    // [MODIFIED] 멤버 여부 확인
    @Transactional(readOnly = true)
    public boolean isUserInProject(String userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        return project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));
    }
}