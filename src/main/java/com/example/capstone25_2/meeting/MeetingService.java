package com.example.capstone25_2.meeting;

import com.example.capstone25_2.meeting.dto.MeetingCreateRequestDto;
import com.example.capstone25_2.meeting.dto.MeetingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public Long createMeeting(MeetingCreateRequestDto requestDto) {

        Meeting meeting = requestDto.toEntity();
        meeting.setCreatedDate(LocalDate.now());
        Meeting savedMeeting = meetingRepository.save(meeting);

        return savedMeeting.getId();

    }

    public List<MeetingResponseDto> getAllMeetings() {

        List<Meeting> meetings = meetingRepository.findAll();

        return meetings.stream()
                .map(meeting -> new MeetingResponseDto(meeting))
                .collect(Collectors.toList());
    }

    public MeetingResponseDto getMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회의록이 없습니다. id=" + id));

                return new MeetingResponseDto(meeting);
    }
}
