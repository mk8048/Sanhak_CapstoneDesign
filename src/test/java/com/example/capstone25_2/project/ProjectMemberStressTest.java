package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserRepository;
import com.example.capstone25_2.user.dto.UserSignupRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(true) // 테스트 결과를 DB에서 확인하고 싶다면 false, 아니면 true
class ProjectMemberStressTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("프로젝트 멤버 1000명 추가 스트레스 테스트")
    void addManyMembersTest() {
        // 1. Owner 생성
        String ownerId = "owner_" + System.currentTimeMillis();
        createDummyUser(ownerId);

        // 2. 프로젝트 생성
        AddProjectRequest request = new AddProjectRequest();
        request.setProjectName("Stress Test Project");
        request.setDescription("Testing with many members");
        request.setDeadline(LocalDate.now().plusDays(30));

        Project project = projectService.save(ownerId, request);
        Long projectId = project.getProjectId();

        System.out.println("=== 프로젝트 생성 완료: ID=" + projectId + " ===");

        // 3. 더미 멤버 1000명 생성
        int memberCount = 1000;
        List<String> memberIds = new ArrayList<>();
        System.out.println("=== 더미 유저 " + memberCount + "명 생성 시작 ===");

        long startUserCreation = System.currentTimeMillis();
        for (int i = 0; i < memberCount; i++) {
            String memberId = "user_" + System.currentTimeMillis() + "_" + i;
            createDummyUser(memberId);
            memberIds.add(memberId);
        }
        long endUserCreation = System.currentTimeMillis();
        System.out.println("=== 더미 유저 생성 완료 (" + (endUserCreation - startUserCreation) + "ms) ===");

        // 4. 멤버 초대 (성능 측정)
        System.out.println("=== 멤버 초대 시작 ===");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (String memberId : memberIds) {
            projectService.inviteMember(projectId, memberId);
        }

        stopWatch.stop();
        System.out.println("=== 멤버 초대 완료 ===");
        System.out.println("총 소요 시간: " + stopWatch.getTotalTimeSeconds() + "초");
        System.out.println("평균 소요 시간(건당): " + (stopWatch.getTotalTimeMillis() / (double) memberCount) + "ms");

        // 5. 프로젝트 조회 성능 측정 (N+1 문제 확인용)
        System.out.println("=== 프로젝트 조회 (N+1 확인) ===");
        long startQuery = System.currentTimeMillis();

        // 임의의 멤버로 프로젝트 목록 조회
        projectService.findProjectsByUserId(memberIds.get(0));

        long endQuery = System.currentTimeMillis();
        System.out.println("프로젝트 조회 소요 시간: " + (endQuery - startQuery) + "ms");
    }

    private void createDummyUser(String id) {
        UserSignupRequestDto dto = new UserSignupRequestDto();
        dto.setId(id);
        dto.setPassword("password");
        dto.setName("Name_" + id);
        dto.setEmail(id + "@test.com");
        dto.setPhone("010-0000-0000");
        // dto.setFocusMode(false); // DTO에 필드 없음

        User user = User.from(dto);
        userRepository.save(user);
    }
}
