package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectResponse;
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
                prj_name: "비상구"
                description: "산학 협력 캡스톤 디자인"
                users_id: "이민기", "차지만", "최민식", "정휘수"
            }
         */

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveProject);
    }
}
