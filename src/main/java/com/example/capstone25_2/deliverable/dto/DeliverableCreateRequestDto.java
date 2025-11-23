package com.example.capstone25_2.deliverable.dto;

import com.example.capstone25_2.deliverable.Deliverable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DeliverableCreateRequestDto {

    private String name;
    private String description;
    private String fileUrl;
    private String uploader;

    public Deliverable toEntity() {
        return new Deliverable(name, description, fileUrl, uploader, LocalDateTime.now());
    }
}