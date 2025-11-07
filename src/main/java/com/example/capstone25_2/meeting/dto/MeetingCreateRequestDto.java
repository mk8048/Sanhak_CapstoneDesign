package com.example.capstone25_2.meeting.dto;

import lombok.Getter;
import lombok.Setter;
import com.example.capstone25_2.meeting.Meeting;

@Getter
@Setter

public class MeetingCreateRequestDto {

    private String title;
    private String content;
    private String summary;

    public Meeting toEntity() {
        Meeting meeting  = new Meeting();
        meeting.setTitle(this.title);
        meeting.setContent(this.content);
        meeting.setSummary(this.summary);

        return meeting;
    }
}
