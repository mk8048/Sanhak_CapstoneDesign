package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행기 주입

    //RequiredArgsConstructor 대신
    public MemoService(MemoRepository memoRepository, ApplicationEventPublisher eventPublisher) {
        this.memoRepository = memoRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<MemoListResponse> findList() {

        // 1. DB에서 Memo 엔티티 리스트를 최신순으로 조회
        List<Memo> memos = memoRepository.findAllByOrderByModifiedAtDesc();

        // 2. Stream API를 사용하여 List<Memo>를 List<MemoListResponse>로 변환
        return memos.stream()                  // memos 리스트를 스트림으로 변환
                .map(MemoListResponse::new)    // 각 Memo 객체를 MemoListResponse 생성자에 넣어 새 DTO 객체 생성
                // memo -> new MemoListResponse(memo)
                .collect(Collectors.toList()); // 새로 만들어진 DTO 객체들을 모아 리스트로 만듦
    }

    @Transactional
    public Memo save(AddMemoRequest request) {
        Memo newMemo = Memo.from(request);
        Memo savedMemo = memoRepository.save(newMemo);

        //메모 생성 이벤트 발생
        eventPublisher.publishEvent(new MemoEvent(savedMemo, MemoEvent.EventType.CREATED));

        return memoRepository.save(newMemo);
    }
    @Transactional
    public Memo updateList(long id, UpdateMemoListRequest requestContent) {
        /*
        Article aritcle2 = blogRepository.findById(id);
        if (article2 == null) {
            throw new IllegalArgumentException("not found: " + id);
        }
         */
        Memo memo = memoRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: " + id));

        memo.updateList(requestContent);

        // 메모 수정 이벤트 발행
        // JPA 영속성 컨텍스트가 닫히기 전이므로 데이터는 업데이트 상태입니다.
        eventPublisher.publishEvent(new MemoEvent(memo, MemoEvent.EventType.UPDATED));

        return memo;
    }

    @Transactional
    public Memo updateCanvas(long id, UpdateMemoCanvasRequest requestPosition) {

        Memo memo = memoRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: " + id));

        memo.updateCanvas(requestPosition);

        return memo;
    }



    public void delete(long id) {
        memoRepository.deleteById(id);
    }

    public List<MemoCanvasResponse> findCanvas() {

        // 1. DB에서 Memo 엔티티 리스트를 최신순으로 조회
        List<Memo> memos = memoRepository.findAll();

        // 2. Stream API를 사용하여 List<Memo>를 List<MemoListResponse>로 변환
        return memos.stream()                  // memos 리스트를 스트림으로 변환
                .map(MemoCanvasResponse::new)  // 각 Memo 객체를 MemoListResponse 생성자에 넣어 새 DTO 객체 생성
                // memo -> new MemoListResponse(memo)
                .collect(Collectors.toList()); // 새로 만들어진 DTO 객체들을 모아 리스트로 만듦
    }


}
