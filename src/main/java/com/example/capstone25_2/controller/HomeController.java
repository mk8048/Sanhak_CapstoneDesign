package com.example.capstone25_2.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/index")
    public String mainpage(HttpSession session, Model model) {

        String userName = (String) session.getAttribute("userName");
        String userNickname = (String) session.getAttribute("userNickname");

        if(userName == null) {
            return "redirect:/user/login";
        }

        String display_name;
        if(userNickname != null && !userNickname.isEmpty()) {
            display_name = userNickname;
        } else {
            display_name = userName;
        }

        model.addAttribute("displayName", display_name);

        System.err.println("mainpage");
        return "main/index";
    }
}
