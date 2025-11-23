package com.example.capstone25_2.deliverable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Deliverable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String fileUrl;
    private String uploader;
    private LocalDateTime uploadDate;

    public Deliverable(String name, String description, String fileUrl, String uploader, LocalDateTime uploadDate) {
        this.name = name;
        this.description = description;
        this.fileUrl = fileUrl;
        this.uploader = uploader;
        this.uploadDate = uploadDate;
    }
}

/*
    {
        "name": "캡스톤 중간발표 자료",
        "description": "중간발표 때 사용할 PPT 초안입니다.",
        "fileUrl": "https://google.com/drive/...",
        "uploader": "정휘수"
     }
 */