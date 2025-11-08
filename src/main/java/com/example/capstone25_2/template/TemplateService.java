package com.example.capstone25_2.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public List<Template> getTemplates(String category) {
        if(category != null && !category.isEmpty()) {
            return templateRepository.findByCategory(category);
        }
        else {
            return templateRepository.findAll();
        }
    }
}
