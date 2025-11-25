package com.example.capstone25_2.memo.dto;

import com.example.capstone25_2.memo.Memo;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class MemoCanvasResponse {

    private final Long id;
    private final String authorId;
    private final String content;
    private final Integer x_pos;
    private final Integer y_pos;
    private final String color;

    private final LocalDateTime createAt;


    public MemoCanvasResponse(Memo memo) {
        this.id = memo.getId();
        this.authorId = memo.getAuthorId();
        this.content = memo.getContent();
        this.x_pos = memo.getX_pos();
        this.y_pos = memo.getY_pos();
        this.color = memo.getColor();
        this.createAt = memo.getCreatedAt();
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
    public Integer getX_pos() {
        return x_pos;
    }
    public Integer getY_pos() {
        return y_pos;
    }
    public String getColor() {
        return color;
    }
    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
