package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.AddMemoRequest;
import com.example.capstone25_2.memo.dto.UpdateMemoListRequest;
import com.example.capstone25_2.memo.dto.UpdateMemoCanvasRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "memos")
@SuppressWarnings({"LombokGetterMayBeUsed"})
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Integer로 생성 시 1초에 하나씩 메모 생성하면 소진까지 68년 걸림

    @Column(nullable = true)
    private Long projectId;
    @Column(nullable = false)
    private String authorId;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer x_pos;
    @Column(nullable = false)
    private Integer y_pos;
    @Column(nullable = false)
    private String color;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private LocalDateTime modifiedAt;

    // JPA가 엔티티를 DB에 저장하기 직전에 호출됨
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    // DB에 있는 엔티티 데이터가 업데이트되기 직전에 호출됨
    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    protected Memo() {
    }


    // DTO를 Entity로 변환하는 정적 팩토리 메서드
    public static Memo from(AddMemoRequest request) {
        Memo memo = new Memo();

        memo.projectId = request.getProjectId();
        memo.authorId = request.getAuthorId();
        memo.content = request.getContent();
        memo.x_pos = (request.getX_pos() != null) ? request.getX_pos() : 0;
        memo.y_pos = (request.getY_pos() != null) ? request.getY_pos() : 0;
        memo.color = (request.getColor() != null) ? request.getColor() : "#FFFFFF";

        return memo;
    }

    public void updateList(UpdateMemoListRequest requestList) {
        this.content = requestList.getContent();
    }

    public void updateCanvas(UpdateMemoCanvasRequest requestCanvas) {
        this.content = requestCanvas.getContent();
        this.x_pos = requestCanvas.getX_pos();
        this.y_pos = requestCanvas.getY_pos();
        this.color = requestCanvas.getColor();
    }


    //@getter 대신
    public Long getId() {
        return id;
    }
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
}
