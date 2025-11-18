package com.example.capstone25_2.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubCommitDTO(
        String sha,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Commit commit,

        String html_url
) {


    public String getMessage() {
        return (commit != null) ? commit.message() : null;
    }

    public String getAuthorName() {
        return (commit != null && commit.author() != null) ? commit.author().name() : null;
    }

    public OffsetDateTime getDate() {
        return (commit != null && commit.author() != null) ? commit.author().date() : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Commit(
            CommitAuthor author,
            String message
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommitAuthor(
            String name,
            String email,
            OffsetDateTime date
    ) {}
}