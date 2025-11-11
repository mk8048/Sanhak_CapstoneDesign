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

    @GetMapping("/api/project")
    public ResponseEntity<List<ProjectResponse>> findProject() {
        List<ProjectResponse> projects = projectService.findProject();

        return ResponseEntity.ok()
                .body(projects);
    }

    @PostMapping("/api/project")
    public ResponseEntity<Project> addProject(@RequestBody AddProjectRequest request) {
        Project saveProject = projectService.save(request);

        /*
            {
                projectName: "비상구"
                description: "산학 협력 캡스톤 디자인"
                usersId: "이민기", "차지만", "최민식", "정휘수"
            }
         */

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveProject);
    }

    @DeleteMapping("/api/project/{id}")
    public ResponseEntity<Void> DeleteProject(@PathVariable Long id) {
        projectService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/project/{id}")
    public ResponseEntity<Project> UpdateProjectRequest(@PathVariable long id,
                                                        @RequestBody UpdateProjectRequest request) {
        Project updateProject = projectService.update(id, request);

        return ResponseEntity.ok()
                .body(updateProject);
    }
}
