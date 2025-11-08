package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    //@RequiredArgsConstructor 대신
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectResponse> findProject() {
        List<Project> projects = projectRepository.findAllByOrderByCreatedAtDesc();

        return projects.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Project save(AddProjectRequest request) {
        Project newProject = Project.from(request);

        return projectRepository.save(newProject);
    }
}
