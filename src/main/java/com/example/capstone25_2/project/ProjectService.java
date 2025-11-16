package com.example.capstone25_2.project;

import com.example.capstone25_2.user.UserRepository;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectResponse;
import com.example.capstone25_2.project.dto.UpdateProjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private Long getUserPkId(String userId) {
        // ... (User 엔티티의 getPk_id() 호출은 문제 없음) ...
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        return user.getPk_id();
    }

    public List<ProjectResponse> findProjectsByUserId(String userId) {
        Long userPkId = getUserPkId(userId);

        // ⭐️ ProjectRepository의 수정된 메서드 이름과 일치 ⭐️
        List<Project> projects = projectRepository.findAllByUsersId(userPkId);

        return projects.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean checkUserAccessById(String userId, Long projectId) {
        Long userPkId = getUserPkId(userId);

        // ID로 프로젝트 조회 (Project ID는 고유하므로 NonUniqueResultException 발생 안 함)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // Project의 usersId (생성자 PK)가 현재 로그인한 사용자 PK와 일치하는지 확인
        if (project.getUsersId() != null && project.getUsersId().equals(userPkId)) {
            return true;
        }

        // 권한이 없으면 false 대신 SecurityException을 던집니다. (WebController에서 처리)
        throw new SecurityException("해당 프로젝트에 접근 권한이 없습니다.");
    }

    // ⭐️ [NEW] ID 기반으로 프로젝트 이름 조회 ⭐️
    @Transactional(readOnly = true)
    public String getProjectNameById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        return project.getProjectName();
    }

    @Transactional
    public Project save(String creatorId, AddProjectRequest request) {
        Long creatorPkId = getUserPkId(creatorId);

        Project newProject = Project.from(request);

        // 🚨 이 setUsersId(Long id) 메서드가 Project 엔티티에 추가되어야 합니다.
        newProject.setUsersId(creatorPkId);

        return projectRepository.save(newProject);
    }

    @Transactional
    public void delete(long id) {
        projectRepository.deleteById(id);
    }

    @Transactional
    public Project update(Long id, UpdateProjectRequest request) {
        // ⭐️ findById(Long id)는 JpaRepository가 기본 제공하므로 문제 없습니다. ⭐️
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: " + id));

        // 🚨 Project Entity에 setUsersId(Long id) 메서드가 없다면, 이 부분은 수정하지 않고 넘어가야 합니다.
        // 현재 update 로직은 usersId를 변경하지 않는 것으로 가정하고 그대로 둡니다.
        project.update(request);

        return project;
    }
}