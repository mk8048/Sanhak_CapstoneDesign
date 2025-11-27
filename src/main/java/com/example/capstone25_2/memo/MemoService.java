package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.*;
import com.example.capstone25_2.project.ProjectService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ProjectService projectService; // 필드 선언은 잘 하셨습니다.

    // ⭐️ [수정] 생성자 파라미터에 projectService를 추가해야 합니다 ⭐️
    public MemoService(MemoRepository memoRepository,
                       ApplicationEventPublisher eventPublisher,
                       ProjectService projectService) { // 여기 추가!
        this.memoRepository = memoRepository;
        this.eventPublisher = eventPublisher;
        this.projectService = projectService; // 여기 할당!
    }

    // (또는 생성자를 다 지우고 클래스 위에 @RequiredArgsConstructor 를 붙여도 됩니다)

    public List<MemoListResponse> findList() {
        // (참고: 실제로는 여기서도 findByProjectId(...)로 필터링해야 특정 프로젝트 메모만 보입니다)
        List<Memo> memos = memoRepository.findAllByOrderByModifiedAtDesc();
        return memos.stream()
                .map(MemoListResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Memo save(AddMemoRequest request, String userId) { // userId 추가됨 (굿!)
        // 🛑 쓰기 권한 검사
        projectService.validateWriteAccess(request.getProjectId(), userId);

        Memo newMemo = Memo.from(request);

        Memo savedMemo = memoRepository.save(newMemo);
        eventPublisher.publishEvent(new MemoEvent(savedMemo, MemoEvent.EventType.CREATED));
        return savedMemo;
    }

    @Transactional
    public Memo updateList(long id, UpdateMemoListRequest requestContent, String userId) { // userId 추가됨
        Memo memo = memoRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: " + id));

        // 🛑 쓰기 권한 검사
        projectService.validateWriteAccess(memo.getProjectId(), userId);

        memo.updateList(requestContent);
        eventPublisher.publishEvent(new MemoEvent(memo, MemoEvent.EventType.UPDATED));
        return memo;
    }

    @Transactional
    public Memo updateCanvas(long id, UpdateMemoCanvasRequest requestPosition, String userId) { // userId 추가됨
        Memo memo = memoRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: " + id));

        // 🛑 쓰기 권한 검사
        projectService.validateWriteAccess(memo.getProjectId(), userId);

        memo.updateCanvas(requestPosition);
        return memo;
    }

    @Transactional
    public void delete(long id, String userId) { // userId 추가됨
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        // 🛑 쓰기 권한 검사
        projectService.validateWriteAccess(memo.getProjectId(), userId);

        memoRepository.delete(memo);
    }

    public List<MemoCanvasResponse> findCanvas() {
        // (참고: 여기서도 findByProjectId(...) 사용 권장)
        List<Memo> memos = memoRepository.findAll();
        return memos.stream()
                .map(MemoCanvasResponse::new)
                .collect(Collectors.toList());
    }
}