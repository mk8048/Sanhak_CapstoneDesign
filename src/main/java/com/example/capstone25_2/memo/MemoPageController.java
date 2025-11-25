package com.example.capstone25_2.memo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemoPageController {

    @GetMapping("/memo")
    public String memoPage(Model model) {
        model.addAttribute("pageTitle", "메모장");
        return "memo/memo";
    }
}
