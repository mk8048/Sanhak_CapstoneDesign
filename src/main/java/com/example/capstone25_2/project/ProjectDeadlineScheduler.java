/*
package com.example.capstone25_2.project;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProjectDeadlineScheduler {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProjectDeadlineScheduler(ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void checkDeadlines() {
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        List<Project> projects = projectRepository.findByDeadline(threeDaysLater);

        for (Project project : projects) {
            eventPublisher.publishEvent(new ProjectDeadlineEvent(project));
        }
    }
}
*/