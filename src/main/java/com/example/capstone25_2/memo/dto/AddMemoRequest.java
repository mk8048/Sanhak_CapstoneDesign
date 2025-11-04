package com.example.capstone25_2.memo.dto;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class AddMemoRequest {
    private Integer project_id;
    private Integer author_id;
    private String content;
    private Integer x_pos;
    private Integer y_pos;
    private String color;

    //@getter 대신
    public Integer getProject_id() {
        return project_id;
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

    //@setter 대신
    public void  setProject_id(Integer project_id) {
        this.project_id = project_id;
    }
    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setX_pos(Integer x_pos) {
        this.x_pos = x_pos;
    }
    public void setY_pos(Integer y_pos) {
        this.y_pos = y_pos;
    }
    public void setColor(String color) {
        this.color = color;
    }
}
