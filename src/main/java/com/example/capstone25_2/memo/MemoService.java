package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.*;
import com.example.capstone25_2.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ProjectService projectService;

    // 전체 메모 리스트 조회 (수정일 내림차순)
    public List<MemoListResponse> findList() {
        List<Memo> memos = memoRepository.findAllByOrderByModifiedAtDesc();
        return memos.stream()
                .map(MemoListResponse::new)
                .collect(Collectors.toList());
    }

    // 캔버스용 전체 메모 조회
    public List<MemoCanvasResponse> findCanvas() {
        List<Memo> memos = memoRepository.findAll();
        return memos.stream()
                .map(MemoCanvasResponse::new)
                .collect(Collectors.toList());
    }

    // 메모 생성
    @Transactional
    public Memo save(AddMemoRequest request, String userId) {
        projectService.validateWriteAccess(request.getProjectId(), userId);

        Memo newMemo = Memo.from(request);

        Memo savedMemo = memoRepository.save(newMemo);
        eventPublisher.publishEvent(new MemoEvent(savedMemo, MemoEvent.EventType.CREATED));

        return savedMemo;
    }

    // 리스트 뷰에서의 메모 내용 수정
    @Transactional
    public Memo updateList(long id, UpdateMemoListRequest requestContent, String userId) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        projectService.validateWriteAccess(memo.getProjectId(), userId);

        memo.updateList(requestContent);
        eventPublisher.publishEvent(new MemoEvent(memo, MemoEvent.EventType.UPDATED));

        return memo;
    }

    // 캔버스 뷰에서의 메모 위치 및 색상 수정
    @Transactional
    public Memo updateCanvas(long id, UpdateMemoCanvasRequest requestPosition, String userId) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        projectService.validateWriteAccess(memo.getProjectId(), userId);

        memo.updateCanvas(requestPosition);
        return memo;
    }

    // 메모 삭제
    @Transactional
    public void delete(long id, String userId) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        projectService.validateWriteAccess(memo.getProjectId(), userId);

        memoRepository.delete(memo);
    }
}