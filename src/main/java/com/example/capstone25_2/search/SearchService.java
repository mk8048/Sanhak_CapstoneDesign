package com.example.capstone25_2.search;

import com.example.capstone25_2.deliverable.Deliverable;
import com.example.capstone25_2.deliverable.DeliverableRepository;
import com.example.capstone25_2.meeting.Meeting;
import com.example.capstone25_2.meeting.MeetingRepository;
import com.example.capstone25_2.memo.Memo;
import com.example.capstone25_2.memo.MemoRepository;
import com.example.capstone25_2.project.Project;
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.search.dto.SearchResultDto;
import com.example.capstone25_2.task.Task;
import com.example.capstone25_2.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DeliverableRepository deliverableRepository;
    private final ProjectRepository projectRepository;
    private final MemoRepository memoRepository;
    private final MeetingRepository meetingRepository;
    private final TaskRepository taskRepository;

    public List<SearchResultDto> search(String keyword) {
        List<SearchResultDto> results = new ArrayList<>();

        // 산출물 검색
        List<Deliverable> deliverables = deliverableRepository
                .findByNameContainingOrDescriptionContaining(keyword, keyword);
        for (Deliverable d : deliverables) {
            results.add(SearchResultDto.builder()
                    .type("산출물")
                    .id(d.getId())
                    .title(d.getName())
                    .description(truncate(d.getDescription(), 100))
                    .date(d.getUploadDate())
                    .metadata("업로더: " + d.getUploader())
                    .url("/deliverable/test?tab=all")
                    .build());
        }

        // 프로젝트 검색
        List<Project> projects = projectRepository
                .findByProjectNameContainingOrDescriptionContaining(keyword, keyword);
        for (Project p : projects) {
            results.add(SearchResultDto.builder()
                    .type("프로젝트")
                    .id(p.getProjectId())
                    .title(p.getProjectName())
                    .description(truncate(p.getDescription(), 100))
                    .date(p.getCreatedAt())
                    .metadata("멤버 수: " + p.getMembers().size())
                    .url("/project/" + p.getProjectId())
                    .build());
        }

        // 메모 검색
        List<Memo> memos = memoRepository.findByContentContaining(keyword);
        for (Memo m : memos) {
            results.add(SearchResultDto.builder()
                    .type("메모")
                    .id(m.getId())
                    .title("메모 #" + m.getId())
                    .description(truncate(m.getContent(), 100))
                    .date(m.getModifiedAt())
                    .metadata("작성자 ID: " + m.getAuthorId())
                    .url("/memo")
                    .build());
        }

        // 회의록 검색
        List<Meeting> meetings = meetingRepository
                .findByTitleContainingOrContentContainingOrSummaryContaining(keyword, keyword, keyword);
        for (Meeting m : meetings) {
            results.add(SearchResultDto.builder()
                    .type("회의록")
                    .id(m.getId())
                    .title(m.getTitle())
                    .description(truncate(m.getSummary() != null ? m.getSummary() : m.getContent(), 100))
                    .date(m.getCreatedDate() != null ? m.getCreatedDate().atStartOfDay() : null)
                    .metadata("회의 날짜: " + (m.getCreatedDate() != null ? m.getCreatedDate().toString() : "미정"))
                    .url("/meeting")
                    .build());
        }

        // 작업 검색
        List<Task> tasks = taskRepository.findByTitleContaining(keyword);
        for (Task t : tasks) {
            results.add(SearchResultDto.builder()
                    .type("작업")
                    .id(t.getId())
                    .title(t.getTitle())
                    .description(t.isCompleted() ? "완료됨" : "진행 중")
                    .date(null) // Task에는 날짜 필드가 없음
                    .metadata("프로젝트 ID: " + t.getProjectId())
                    .url("/project/" + t.getProjectId())
                    .build());
        }

        // 날짜 기준 내림차순 정렬 (최신순)
        return results.stream()
                .sorted(Comparator.comparing(SearchResultDto::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    // 문자열을 지정된 길이로 자르는 헬퍼 메서드
    private String truncate(String text, int maxLength) {
        if (text == null)
            return "";
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength) + "...";
    }
}
