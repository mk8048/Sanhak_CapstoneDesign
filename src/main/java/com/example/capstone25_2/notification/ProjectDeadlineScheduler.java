package com.example.capstone25_2.notification;

import com.example.capstone25_2.project.Project;
import com.example.capstone25_2.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectDeadlineScheduler {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    //매일 오전 9시에 실행 (초 분 시 일 월 요일)
    //@Scheduled(fixedRate = 10000) 테스트용 : 10초마다
    @Scheduled(cron = "0 0 9 * * *")
    public void checkProjectDeadlines() {
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        List<Project> imminentProjects = projectRepository.findByDeadline(threeDaysLater);

        if (imminentProjects.isEmpty()) {
            log.info("마감 임박 프로젝트가 없습니다.");
            return;
        }

        for (Project project : imminentProjects) {
            log.info("이벤트 발행: 프로젝트 ID {}", project.getProjectId());

            //이벤트 발행하면 @eventListener 자동 실행
            eventPublisher.publishEvent(new ProjectDeadlineEvent(project, 3));
        }
    }
}
