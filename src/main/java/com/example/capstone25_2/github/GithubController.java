package com.example.capstone25_2.github;

import com.example.capstone25_2.github.GithubService;
import com.example.capstone25_2.github.dto.GithubCommitDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.*;


@Controller
@RequestMapping("/github/commits")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
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
        return "commits";
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

        // KST 하루 구간 → API 호출은 UTC로 변환
        java.time.OffsetDateTime sinceUtc = null;            // KST 00:00 → UTC
        java.time.OffsetDateTime untilUtcExclusive = null;   // 다음날 KST 00:00 → UTC (배타)

        // 화면/로그용 표시 범위 (KST 00:00 ~ 23:59:59)
        String displaySince = since;
        String displayUntil = until;

        try {
            if (since != null && !since.isBlank()) {
                var startKst = java.time.LocalDate.parse(since).atStartOfDay(KST); // yyyy-MM-dd 00:00:00 KST
                sinceUtc = startKst.withZoneSameInstant(UTC).toOffsetDateTime();
            }
            if (until != null && !until.isBlank()) {
                var endKstExclusive = java.time.LocalDate.parse(until)
                        .plusDays(1)
                        .atStartOfDay(KST); // 다음날 00:00:00 KST (배타)
                untilUtcExclusive = endKstExclusive.withZoneSameInstant(UTC).toOffsetDateTime();
            }

            if (sinceUtc != null && untilUtcExclusive != null && sinceUtc.isAfter(untilUtcExclusive)) {
                var tmp = sinceUtc;
                sinceUtc = untilUtcExclusive.minusDays(1); // 대략 하루 앞당김
                untilUtcExclusive = tmp.plusDays(1);
            }

        } catch (Exception e) {
            System.out.println("날짜 파싱 오류: " + e.getMessage());
        }

        System.out.println("📅 KST 표시범위: " +
                (displaySince != null ? displaySince + " 00:00:00" : "null") + " ~ " +
                (displayUntil != null ? displayUntil + " 23:59:59" : "null"));
        System.out.println("↳ API 호출범위(UTC, until exclusive): since=" + sinceUtc + ", until(excl)=" + untilUtcExclusive);

        List<GithubCommitDTO> commits = githubService.getCommits(
                null, null, null,
                sinceUtc, untilUtcExclusive,
                100, 1
        );

        Map<String, Long> countMap = commits.stream()
                .filter(c -> c.author() != null && c.author().login() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        c -> c.author().login(), java.util.stream.Collectors.counting()));

        model.addAttribute("commitData", countMap);
        model.addAttribute("since", displaySince);
        model.addAttribute("until", displayUntil);

        return "github/commits_chart";
    }

}

