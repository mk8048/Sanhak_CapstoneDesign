package com.example.capstone25_2.meeting;

import com.example.capstone25_2.meeting.dto.MeetingCreateRequestDto;
import com.example.capstone25_2.meeting.dto.MeetingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public Long createMeeting(@RequestBody MeetingCreateRequestDto requestDto) {
        return meetingService.createMeeting(requestDto);
    }

    @GetMapping
    public List<MeetingResponseDto> getAllMeetings() {
        return meetingService.getAllMeetings();
    }

    @GetMapping("/{id}")
    public MeetingResponseDto getMeetingById(@PathVariable Long id) {
        return meetingService.getMeetingById(id);
    }
}
