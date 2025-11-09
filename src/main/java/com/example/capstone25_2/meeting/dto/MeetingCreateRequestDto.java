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

/*
Postman 테스트용 JSON 예시:
{
  "title": "주간 회의",
  "content": "진행 상황 공유 및 다음 주 계획 논의",
  "summary": "모두 정상 진행 중, 특이사항 없음"
}
*/