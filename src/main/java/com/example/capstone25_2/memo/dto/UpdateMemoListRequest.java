package com.example.capstone25_2.memo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class UpdateMemoListRequest {

    private final String content;

    @JsonCreator
    public UpdateMemoListRequest(
            @JsonProperty("content") String content) {
        this.content = content;
    }

    //@getter 대신
    public String getContent() {
        return content;
    }
}
