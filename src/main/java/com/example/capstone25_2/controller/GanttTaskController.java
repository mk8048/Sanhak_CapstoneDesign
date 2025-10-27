package com.example.capstone25_2.controller;

import com.example.capstone25_2.service.GanttTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gantt")
@RequiredArgsConstructor
public class GanttTaskController {

    private final GanttTaskService ganttTaskService;


}