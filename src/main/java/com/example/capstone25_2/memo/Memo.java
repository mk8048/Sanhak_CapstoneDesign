package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.AddMemoRequest;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "memos")
@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Integer project_id;
    @Column(nullable = false)
    private Integer author_id;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer x_pos;
    @Column(nullable = false)
    private Integer y_pos;
    @Column(nullable = false)
    private String color;
    @Column(updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    // DTO를 Entity로 변환하는 정적 팩토리 메서드
    public static Memo from(AddMemoRequest request) {
        Memo memo = new Memo();
        memo.project_id = request.getProject_id();
        memo.author_id = request.getAuthor_id();
        memo.content = request.getContent();
        memo.x_pos = request.getX_pos();
        memo.y_pos = request.getY_pos();
        memo.color = request.getColor();

        return memo;
    }




    //@getter 대신
    public Integer getId() {
        return id;
    }
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
    public void  setId(Integer id) {
        this.id = id;
    }
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
