package com.example.capstone25_2.deliverable;

import com.example.capstone25_2.deliverable.dto.DeliverableCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliverables")
@RequiredArgsConstructor
public class DeliverableController {

    private final DeliverableService deliverableService;

    @PostMapping
    public ResponseEntity<Long> createDeliverable(@RequestBody DeliverableCreateRequestDto requestDto) {
        return ResponseEntity.ok(deliverableService.createDeliverable(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<Deliverable>> getAllDeliverables() {
        return ResponseEntity.ok(deliverableService.getAllDeliverables());
    }
}

// Separate controller for page routing (not API)
@Controller
@RequestMapping("/deliverable")
@RequiredArgsConstructor
class DeliverablePageController {

    @GetMapping("/test")
    public String deliverableTestPage() {
        return "deliverable/deliverable-test";
    }
}