package com.example.capstone25_2.memo.dto;

import com.example.capstone25_2.memo.Memo;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class MemoArrayResponse {


    private final Integer id;
    private final Integer author_id;
    private final String content;


    public MemoArrayResponse(Memo memo) {
        this.id = memo.getId();
        this.author_id = memo.getAuthor_id();
        this.content = memo.getContent();
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

}
