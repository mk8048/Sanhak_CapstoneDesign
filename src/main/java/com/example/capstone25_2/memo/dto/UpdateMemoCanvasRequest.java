package com.example.capstone25_2.memo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class UpdateMemoCanvasRequest {

    private final String content;
    private final Integer x_pos;
    private final Integer y_pos;
    private final String color;

    @JsonCreator
    public UpdateMemoCanvasRequest(@JsonProperty("content") String content,
                                   @JsonProperty("x_pos") Integer x_pos,
                                   @JsonProperty("y_pos") Integer y_pos,
                                   @JsonProperty("color") String color) {
        this.content = content;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.color = color;
    }

    //@getter 대신
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
