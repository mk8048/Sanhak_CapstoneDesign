package com.example.capstone25_2.memo.dto;

import com.example.capstone25_2.memo.Memo;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class MemoListResponse {

    private final Long id;
    private final Integer author_id;
    private final String content;
    private final LocalDateTime modifiedAt;

    // Memo 엔티티를 받아서 MemoListResponse DTO를 생성하는 생성자
    public MemoListResponse(Memo memo) {
        this.id = memo.getId();
        this.author_id = memo.getAuthor_id();
        this.content = memo.getContent();
        this.modifiedAt = memo.getModifiedAt();
    }

    //@getter 대신
    public Long getId() {
        return id;
    }
    public Integer getAuthor_id() {
        return author_id;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getModifidAt() {
        return modifiedAt;
    }
}
