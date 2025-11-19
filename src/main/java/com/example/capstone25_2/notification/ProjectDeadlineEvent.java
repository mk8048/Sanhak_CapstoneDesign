package com.example.capstone25_2.notification;

import com.example.capstone25_2.project.Project;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class ProjectDeadlineEvent {
    private final Project project;
    private final int daysUntilDeadline;

    public ProjectDeadlineEvent(Project project, int daysUntilDeadline) {
        this.project = project;
        this.daysUntilDeadline = daysUntilDeadline;
    }

    public Project getProject() {
        return project;
    }

    public int getDaysUntilDeadline() {
        return daysUntilDeadline;
    }
}