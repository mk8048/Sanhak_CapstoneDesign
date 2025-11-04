package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.AddMemoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoController {
    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping("/api/memo")
    public ResponseEntity<Memo> addMemo(@RequestBody AddMemoRequest request) {
        Memo savedMemo = memoService.save(request);

        /*
        POST /api/memo
        content-type: application/json
        {
            "project_id": 1,
            "author_id": 123,
            "content": "스프링 부트 회의 준비",
            "x_pos": 100,
            "y_pos": 150,
            "color": "#FFFF00"
        }
         */

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedMemo);
    }



}
