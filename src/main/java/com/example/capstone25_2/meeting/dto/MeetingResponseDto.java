package com.example.capstone25_2.meeting.dto;

import com.example.capstone25_2.meeting.Meeting;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MeetingResponseDto {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private LocalDate createdDate;

    public MeetingResponseDto(Meeting entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.summary = entity.getSummary();
        this.createdDate = entity.getCreatedDate();
    }

}
