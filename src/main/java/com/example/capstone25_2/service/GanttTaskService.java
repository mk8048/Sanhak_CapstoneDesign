package com.example.capstone25_2.service;

import com.example.capstone25_2.repository.GanttTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GanttTaskService {

    private final GanttTaskRepository ganttTaskRepository;



}