package com.example.capstone25_2.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubCommitDTO(
        String sha,
        Commit commit,
        User author,
        User committer,
        String html_url
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Commit(
            CommitAuthor author,
            CommitAuthor committer,
            String message
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommitAuthor(
            String name,
            String email,
            OffsetDateTime date
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(
            String login,
            String avatar_url,
            String html_url
    ) {}
}
