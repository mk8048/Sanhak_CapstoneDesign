package com.example.capstone25_2.github;

import com.example.capstone25_2.github.dto.GithubCommitDTO;
import com.example.capstone25_2.github.dto.GithubRepoDTO;
import com.example.capstone25_2.project.ProjectService; // ⭐️ ProjectService 추가
import jakarta.servlet.http.HttpSession; // ⭐️ HttpSession 추가
import lombok.RequiredArgsConstructor; // ⭐️ Lombok 생성자 주입 사용 권장
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/github/commits")
@RequiredArgsConstructor // ⭐️ final 필드 생성자 자동 주입
public class GithubController {

    private final GithubService githubService;
    private final ProjectService projectService; // ⭐️ 프로젝트 이름 조회를 위해 추가

    // ⭐️⭐️⭐️ [추가] 모든 요청 시 레이아웃 공통 데이터(사용자, 프로젝트) 주입 ⭐️⭐️⭐️
    @ModelAttribute
    public void addLayoutAttributes(HttpSession session, Model model) {
        // 1. 사용자 이름/닉네임 처리 (displayName)
        String userName = (String) session.getAttribute("userName");
        String userNickname = (String) session.getAttribute("userNickname");

        if (userName != null) {
            String displayName = (userNickname != null && !userNickname.isEmpty()) ? userNickname : userName;
            model.addAttribute("displayName", displayName);
        }

        // 2. 현재 선택된 프로젝트 정보 처리 (currentProjectId, pageTitle)
        Long projectId = (Long) session.getAttribute("currentProjectId");
        if (projectId != null) {
            model.addAttribute("currentProjectId", projectId);

            // 프로젝트 이름을 조회하여 헤더 타이틀로 설정
            try {
                String projectName = projectService.getProjectNameById(projectId);
                model.addAttribute("pageTitle", projectName);
            } catch (Exception e) {
                // 프로젝트가 삭제되었거나 찾을 수 없는 경우 무시
            }
        }
    }

    // HTML 페이지
    @GetMapping
    public String view(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String repo,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime until,
            @RequestParam(required = false, defaultValue = "20") Integer perPage,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            Model model
    ) {
        List<GithubCommitDTO> commits = githubService.getCommits(
                owner, repo, branch, since, until, perPage, page
        );
        model.addAttribute("commits", commits);
        model.addAttribute("owner", owner);
        model.addAttribute("repo", repo);
        model.addAttribute("branch", branch);
        model.addAttribute("perPage", perPage);
        model.addAttribute("page", page);
        model.addAttribute("koreaTZ", ZoneId.of("Asia/Seoul"));
        return "github/commits";
    }

    // JSON API
    @GetMapping("/api")
    @ResponseBody
    public List<GithubCommitDTO> api(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String repo,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime until,
            @RequestParam(required = false, defaultValue = "20") Integer perPage,
            @RequestParam(required = false, defaultValue = "1") Integer page
    ) {
        return githubService.getCommits(owner, repo, branch, since, until, perPage, page);
    }

    // 팀원별 커밋 차트 페이지
    @GetMapping("/chart")
    public String chart(
            @RequestParam(required = false) String since,  // yyyy-MM-dd (KST 기준)
            @RequestParam(required = false) String until,  // yyyy-MM-dd (KST 기준)
            Model model) {

        final ZoneId KST = ZoneId.of("Asia/Seoul");
        final java.time.ZoneOffset UTC = java.time.ZoneOffset.UTC;

        if ((since == null || since.isBlank()) && (until == null || until.isBlank())) {
            String today = LocalDate.now(KST).toString();
            since = today;
            until = today;
        }

        java.time.OffsetDateTime sinceUtc = null;
        java.time.OffsetDateTime untilUtcExclusive = null;

        String displaySince = since;
        String displayUntil = until;

        try {
            if (since != null && !since.isBlank()) {
                var startKst = java.time.LocalDate.parse(since).atStartOfDay(KST);
                sinceUtc = startKst.withZoneSameInstant(UTC).toOffsetDateTime();
            }
            if (until != null && !until.isBlank()) {
                var endKstExclusive = java.time.LocalDate.parse(until)
                        .plusDays(1)
                        .atStartOfDay(KST);
                untilUtcExclusive = endKstExclusive.withZoneSameInstant(UTC).toOffsetDateTime();
            }

            if (sinceUtc != null && untilUtcExclusive != null && sinceUtc.isAfter(untilUtcExclusive)) {
                var tmp = sinceUtc;
                sinceUtc = untilUtcExclusive.minusDays(1);
                untilUtcExclusive = tmp.plusDays(1);
            }

        } catch (Exception e) {
            System.out.println("날짜 파싱 오류: " + e.getMessage());
        }

        List<GithubCommitDTO> commits = githubService.getCommits(
                null, null, null,
                sinceUtc, untilUtcExclusive,
                100, 1
        );

        Map<String, Long> countMap = commits.stream()
                .filter(c -> c.getAuthorName() != null)
                .collect(Collectors.groupingBy(
                        GithubCommitDTO::getAuthorName,
                        Collectors.counting()
                ));

        model.addAttribute("commitData", countMap);
        model.addAttribute("since", displaySince);
        model.addAttribute("until", displayUntil);

        return "github/commits_chart";
    }

    @GetMapping("/recommend")
    public String recommend(
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        String searchKeyword = (keyword != null) ? keyword : "Spring Boot Project";

        List<GithubRepoDTO> repos = githubService.searchOpenSourceProjects(searchKeyword);

        model.addAttribute("repos", repos);
        model.addAttribute("keyword", searchKeyword);

        return "github/recommend";
    }
}