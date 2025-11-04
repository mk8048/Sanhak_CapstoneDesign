package com.example.capstone25_2.memo.dto;

import com.example.capstone25_2.memo.Memo;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class MemoMatrixResponse {

    private final Integer id;
    private final Integer author_id;
    private final String content;
    private final Integer x_pos;
    private final Integer y_pos;
    private final String color;


    public MemoMatrixResponse(Memo memo) {
        this.id = memo.getId();
        this.author_id = memo.getAuthor_id();
        this.content = memo.getContent();
        this.x_pos = memo.getX_pos();
        this.y_pos = memo.getY_pos();
        this.color = memo.getColor();
    }




    //@getter 대신
    public Integer getId() {
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
}
