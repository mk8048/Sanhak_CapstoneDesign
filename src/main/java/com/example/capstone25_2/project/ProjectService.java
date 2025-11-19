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
import java.util.Optional; // Optional import 추가

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Helper: 로그인 ID(String)를 User PK(Long)로 변환 (기존 유지)
    private Long getUserPkId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        return user.getPk_id();
    }

    // Helper: User PK(Long)를 User Login ID(String)로 변환 (기존 유지)
    private String getLoginIdByUserPk(Long userPkId) {
        // JpaRepository의 기본 findById(Long)을 사용하여 PK로 User 조회
        User user = userRepository.findById(userPkId)
                .orElseThrow(() -> new IllegalStateException("프로젝트 소유자 정보를 찾을 수 없습니다."));
        return user.getId();
    }

    // [MODIFIED] 프로젝트 목록 조회: memberIds 기준으로 변경
    public List<ProjectResponse> findProjectsByUserId(String userId) {
        Long userPkId = getUserPkId(userId);
        List<Project> projects = projectRepository.findAllByMemberIdsContaining(userId);

        return projects.stream()
                .map(project -> {
                    // 1. 소유자 로그인 ID 조회
                    String ownerLoginId = getLoginIdByUserPk(project.getUsersId());

                    // 2. 모든 멤버 ID (Set<String>)를 가져옵니다.
                    Set<String> memberSet = project.getMemberIds();

                    // 3. 정렬된 리스트 생성 (소유자 우선)
                    List<String> sortedMembers = new ArrayList<>();
                    if (memberSet.contains(ownerLoginId)) {
                        sortedMembers.add(ownerLoginId); // 1. 소유자 ID를 맨 앞에 추가
                    }

                    // 4. 소유자를 제외한 나머지 멤버 추가
                    memberSet.stream()
                            .filter(id -> !id.equals(ownerLoginId))
                            .sorted() // (알파벳 순 정렬)
                            .forEach(sortedMembers::add);

                    // 5. 쉼표(,)로 구분된 문자열로 변환
                    String allMembers = String.join(", ", sortedMembers);

                    // 6. 수정된 DTO 생성자 호출
                    return new ProjectResponse(project, ownerLoginId, allMembers);
                })
                .collect(Collectors.toList());
    }

    // [MODIFIED] 접근 권한 확인: memberIds 기준으로 변경
    @Transactional(readOnly = true)
    public boolean checkUserAccessById(String userId, Long projectId) {
        // ID로 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // Project의 memberIds Set에 현재 로그인한 사용자 ID가 포함되는지 확인
        if (project.getMemberIds().contains(userId)) {
            return true;
        }

        throw new SecurityException("해당 프로젝트에 접근 권한이 없습니다.");
    }

    // [MODIFIED] 프로젝트 생성: memberIds에 생성자 ID 추가
    @Transactional
    public Project save(String creatorId, AddProjectRequest request) {
        Long creatorPkId = getUserPkId(creatorId);

        Project newProject = Project.from(request);

        // 1. 소유자 PK 설정
        newProject.setUsersId(creatorPkId);

        // 2. 참여자 목록(memberIds)에 생성자 ID(String) 추가
        newProject.addMember(creatorId);

        return projectRepository.save(newProject);
    }

    // [NEW] 프로젝트 탈퇴 로직 구현
    @Transactional
    public void leaveProject(String userId, Long projectId) {
        Long userPkId = getUserPkId(userId); // PK로 소유자 확인용

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("탈퇴할 프로젝트를 찾을 수 없습니다."));

        if (!project.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("해당 프로젝트의 멤버가 아닙니다.");
        }

        // 소유자(Owner)는 탈퇴 불가 (소유자 PK와 현재 사용자 PK 비교)
        if (project.getUsersId() != null && project.getUsersId().equals(userPkId)) {
            throw new IllegalArgumentException("프로젝트 소유자는 탈퇴할 수 없습니다. 소유권을 위임하거나 프로젝트를 삭제하세요.");
        }

        // 탈퇴 처리: Set에서 사용자 ID를 제거합니다.
        project.removeMember(userId);

        // (저장하지 않아도 @Transactional에 의해 변경 내용이 DB에 반영됨)
    }


    @Transactional
    public void delete(long id) {
        projectRepository.deleteById(id);
    }

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

    @Transactional(readOnly = true)
    public List<User> getProjectMembers(Long projectId) {

        // 1. Project Entity를 조회합니다. (memberIds Set을 가져오기 위해)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 2. 멤버들의 로그인 ID (String) Set을 가져옵니다.
        Set<String> memberLoginIds = project.getMemberIds();

        if (memberLoginIds.isEmpty()) {
            return List.of(); // 멤버가 없으면 빈 리스트 반환
        }

        // 3. UserRepository에 새 쿼리 메서드가 필요: List<User> findAllByIdIn(Collection<String> ids);
        List<User> members = userRepository.findAllByIdIn(memberLoginIds);

        // 임시로, Service가 User Repository를 통해 memberLoginIds에 해당하는 User 객체 목록을 반환한다고 가정하겠습니다.

        return userRepository.findAllByIdIn(memberLoginIds); // 이 메서드가 UserRepository에 추가되어야 합니다.
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
    }

    @Transactional
    public void inviteMember(Long projectId, String emailOrId) {

        // 1. 초대할 사용자 조회
        // ID로 먼저 찾아보고, 없으면 Email로 찾습니다.
        User userToInvite = userRepository.findById(emailOrId)
                .orElseGet(() -> userRepository.findByEmail(emailOrId)
                        .orElseThrow(() -> new IllegalArgumentException("'" + emailOrId + "' 사용자를 찾을 수 없습니다.")));

        // 2. 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 3. 이미 참여 중인지 확인
        if (project.getMemberIds().contains(userToInvite.getId())) {
            throw new IllegalArgumentException("이미 참여 중인 멤버입니다.");
        }

        // 4. 프로젝트의 memberIds Set에 사용자 로그인 ID(String) 추가
        project.addMember(userToInvite.getId());

        // (트랜잭션 종료 시 Dirty Checking으로 자동 저장됨)
    }

    @Transactional(readOnly = true)
    public boolean isUserInProject(String userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // Project 엔티티의 memberIds Set에 userId가 포함되어 있는지 확인
        return project.getMemberIds().contains(userId);
    }
}