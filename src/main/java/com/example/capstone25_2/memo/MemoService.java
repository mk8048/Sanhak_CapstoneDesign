package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.AddMemoRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemoService {
    private final MemoRepository memoRepository;

    //RequiredArgsConstructor 대신
    public MemoService(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }

    @Transactional
    public Memo save(AddMemoRequest request) {

        Memo newMemo = Memo.from(request);

        return memoRepository.save(newMemo);
    }
}
