package com.example.capstone25_2.memo;

import com.example.capstone25_2.project.ProjectRole;
import com.example.capstone25_2.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class MemoPageController {

    private final ProjectService projectService;

    // 레이아웃(사이드바/헤더) 구성을 위한 공통 데이터(사용자/프로젝트 정보)를 모델에 주입
    @ModelAttribute
    public void addLayoutAttributes(HttpSession session, Model model) {
        String userName = (String) session.getAttribute("userName");
        String userNickname = (String) session.getAttribute("userNickname");

        if (userName != null) {
            String displayName = (userNickname != null && !userNickname.isEmpty()) ? userNickname : userName;
            model.addAttribute("displayName", displayName);
        }

        Long projectId = (Long) session.getAttribute("currentProjectId");
        if (projectId != null) {
            model.addAttribute("currentProjectId", projectId);

            try {
                String projectName = projectService.getProjectNameById(projectId);
                model.addAttribute("pageTitle", projectName);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    // 메모장 페이지 조회 및 사용자 권한(Role) 정보 전달
    @GetMapping("/memo")
    public String memoPage(HttpSession session, Model model) {
        Long projectId = (Long) session.getAttribute("currentProjectId");
        String userId = (String) session.getAttribute("userId");

        if (projectId == null) {
            return "redirect:/projects";
        }
        if (userId == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("userId", userId);

        ProjectRole role = projectService.getUserRoleInProject(projectId, userId);
        model.addAttribute("userRole", role);
        model.addAttribute("pageTitle", "메모장");

        return "memo/memo";
    }
}