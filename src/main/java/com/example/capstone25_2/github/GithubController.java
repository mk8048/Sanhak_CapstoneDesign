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
}
