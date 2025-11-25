package com.example.capstone25_2.memo.dto;

import com.example.capstone25_2.memo.Memo;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class MemoListResponse {

    private final Long id;
    private final String authorId;
    private final String content;
    private final LocalDateTime modifiedAt;

    // Memo 엔티티를 받아서 MemoListResponse DTO를 생성하는 생성자
    public MemoListResponse(Memo memo) {
        this.id = memo.getId();
        this.authorId = memo.getAuthorId();
        this.content = memo.getContent();
        this.modifiedAt = memo.getModifiedAt();
    }

    //@getter 대신
    public Long getId() {
        return id;
    }
    public String getAuthorId() {
        return authorId;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
}
