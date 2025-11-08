package com.example.capstone25_2.template;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카테고리 (예 : "README", "ISSUE")
    private String category;

    // 제목 (예 : "기본 README 양식")
    private String title;

    // 내용 (예 : "긴 텍스트가 들어갈 수 있어 길이 제한을 늘림")
    @Column(columnDefinition = "TEXT")
    private String content;
}
