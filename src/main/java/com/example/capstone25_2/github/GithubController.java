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
import java.util.stream.Collectors;

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
    public String chart(Model model) {
        // 기본 repo 기준으로 최근 커밋 목록 가져오기
        List<GithubCommitDTO> commits = githubService.getCommits(null, null, null, null, null, 100, 1);

        // author별 커밋 수 계산
        Map<String, Long> countMap = commits.stream()
                .filter(c -> c.author() != null && c.author().login() != null)
                .collect(Collectors.groupingBy(c -> c.author().login(), Collectors.counting()));

        // 차트용 데이터 (x: 작성자, y: 커밋 수)
        model.addAttribute("commitData", countMap);
        return "github/commits_chart";
    }
}
