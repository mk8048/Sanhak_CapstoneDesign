package com.example.capstone25_2.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public List<Template> getTemplates(@RequestParam(required = false) String category) {
        return templateService.getTemplates(category);
    }
}

// Separate controller for page routing (not API)
@Controller
@RequestMapping("/template")
@RequiredArgsConstructor
class TemplatePageController {

    @GetMapping("/test")
    public String templateTestPage() {
        return "template/template-test";
    }
}
