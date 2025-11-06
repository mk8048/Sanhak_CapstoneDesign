package com.example.capstone25_2.memo.dto;

import com.example.capstone25_2.memo.Memo;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class MemoCanvasResponse {

    private final Long id;
    private final Integer author_id;
    private final String content;
    private final Integer x_pos;
    private final Integer y_pos;
    private final String color;

    private final LocalDateTime createAt;


    public MemoCanvasResponse(Memo memo) {
        this.id = memo.getId();
        this.author_id = memo.getAuthor_id();
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
    public Integer getAuthor_id() {
        return author_id;
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
