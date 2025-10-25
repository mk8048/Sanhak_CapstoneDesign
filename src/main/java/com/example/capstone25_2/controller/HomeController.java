package com.example.capstone25_2.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/main")
    public String mainpage() {
        System.err.println("mainpage");
        return "main/index";
    }
}
