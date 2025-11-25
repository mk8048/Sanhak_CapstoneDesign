package com.example.capstone25_2.memo.dto;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class AddMemoRequest {
    private Long projectId;
    private String authorId;
    private String content;
    private Integer x_pos;
    private Integer y_pos;
    private String color;

    //@getter 대신
    public Long getProjectId() {
        return projectId;
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

    //@setter 대신
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
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
