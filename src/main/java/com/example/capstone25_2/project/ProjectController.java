package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectResponse;
import com.example.capstone25_2.project.dto.UpdateProjectRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 1. 프로젝트 목록 조회 (REST)
    @GetMapping("/api/project")
    public ResponseEntity<List<ProjectResponse>> findProject() {

        String currentUserId = "API_USER";

        List<ProjectResponse> projects = projectService.findProjectsByUserId(currentUserId);

        return ResponseEntity.ok()
                .body(projects);
    }

    // 2. 프로젝트 생성 (REST)
    @PostMapping("/api/project")
    public ResponseEntity<Project> addProject(@RequestBody AddProjectRequest request) {

        String tempCreatorId = "API_USER";

        // ProjectService.save()의 시그니처 변경에 맞춰 creatorId 인자 추가
        Project saveProject = projectService.save(tempCreatorId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveProject);
    }

    // 3. 프로젝트 삭제 (REST)
    @DeleteMapping("/api/project/{id}")
    public ResponseEntity<Void> DeleteProject(@PathVariable Long id) {
        projectService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    // 4. 프로젝트 수정 (REST)
    @PutMapping("/api/project/{id}")
    public ResponseEntity<Project> UpdateProjectRequest(@PathVariable long id,
                                                        @RequestBody UpdateProjectRequest request) {
        Project updateProject = projectService.update(id, request);

        return ResponseEntity.ok()
                .body(updateProject);
    }
}