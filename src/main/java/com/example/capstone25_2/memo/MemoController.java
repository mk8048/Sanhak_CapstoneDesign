package com.example.capstone25_2.memo;

import com.example.capstone25_2.memo.dto.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemoController {
    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    private String getUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }


    @GetMapping("/api/memo/canvas")
    public ResponseEntity<List<MemoCanvasResponse>> findCanvasMemo() {

        /*
        List<Memo> memos = memoService.findAll();
        List<MemoCanvasResponse> ret = new ArrayList<>();
        for (Memo memo : memos) {
            MemoCanvasResponse memoCanvasResponse = new MemoCanvasResponse(Memo);
            ret.add(memoCanvasResponse);
        }
        */

        List<MemoCanvasResponse> memos = memoService.findCanvas();

        return ResponseEntity.ok()
                .body(memos);
    }

    @GetMapping("/api/memo/list")
    public ResponseEntity<List<MemoListResponse>> findListMemo() {

        /*
        List<Memo> memos = memoService.findAll();
        List<MemoMatrixResponse> ret = new ArrayList<>();
        for (Memo memo : memos) {
            MemoMatrixResponse memoMatrixResponse = new MemoMatrixResponse(Memo);
            ret.add(memoMatrixResponse);
        }
        */

        List<MemoListResponse> memos_list = memoService.findList();

        return ResponseEntity.ok()
                .body(memos_list);
    }

    @PostMapping("/api/memo")
    public ResponseEntity<Memo> addMemo(@RequestBody AddMemoRequest request, HttpSession session) {
        // ⭐️ userId 전달
        Memo savedMemo = memoService.save(request, getUserId(session));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMemo);
    }

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


    @DeleteMapping("/api/memo/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id, HttpSession session) {
        // ⭐️ userId 전달
        memoService.delete(id, getUserId(session));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/memo/canvas/{id}")
    public ResponseEntity<Memo> UpdateMemoPosition(@PathVariable long id,
                                                   @RequestBody UpdateMemoCanvasRequest request,
                                                   HttpSession session) {
        // ⭐️ userId 전달
        Memo updatePositionMemo = memoService.updateCanvas(id, request, getUserId(session));
        return ResponseEntity.ok().body(updatePositionMemo);
    }

        /*
        {
            "content": "스프링 부트 회의 준비",
            "x_pos": 100,
            "y_pos": 150,
            "color": "#FFFF00"
        }
         */

    @PutMapping("/api/memo/list/{id}")
    public ResponseEntity<Memo> UpdateMemoContent(@PathVariable long id,
                                                  @RequestBody UpdateMemoListRequest request,
                                                  HttpSession session) {
        // ⭐️ userId 전달
        Memo updateContentMemo = memoService.updateList(id, request, getUserId(session));
        return ResponseEntity.ok().body(updateContentMemo);
    }

    /*
        {
            "content": "스프링 부트 회의 준비"
        }
         */
}
