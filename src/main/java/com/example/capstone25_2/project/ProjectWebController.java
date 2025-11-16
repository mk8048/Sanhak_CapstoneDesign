package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectResponse;
import jakarta.servlet.http.HttpSession; // HttpSession import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectWebController {

    private final ProjectService projectService;

    // Helper method: 로그인 상태 확인 및 userId 반환
    private String getUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        return userId;
    }

    // 1. 프로젝트 목록 페이지 조회 (GET /projects)
    @GetMapping("/projects")
    public String getProjectList(HttpSession session, Model model) {
        try {
            String userId = getUserId(session);

            // ⭐️ Service에서 사용자별 프로젝트 목록 조회
            List<ProjectResponse> projects = projectService.findProjectsByUserId(userId);
            model.addAttribute("projectList", projects);

            return "project/project_list";
        } catch (SecurityException e) {
            return "redirect:/user/login"; // 비로그인 시 로그인 페이지로
        }
    }

    // 2. 새 프로젝트 생성 폼 페이지 조회 (GET /project/new)
    @GetMapping("/project/new")
    public String createProjectForm(HttpSession session, Model model) {
        try {
            getUserId(session);
            model.addAttribute("addProjectRequest", new AddProjectRequest());

            return "project/project_new";
        } catch (SecurityException e) {
            return "redirect:/user/login";
        }
    }

    // 3. 새 프로젝트 생성 데이터 처리 (POST /project/create)
    @PostMapping("/project/create")
    public String createProject(@ModelAttribute AddProjectRequest request,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            String creatorId = getUserId(session);

            // ⭐️ Service의 변경된 save 메서드 호출 (creatorId 전달) ⭐️
            projectService.save(creatorId, request);

            redirectAttributes.addFlashAttribute("successMessage", "새 프로젝트가 생성되었습니다.");
            return "redirect:/projects";
        } catch (SecurityException e) {
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/project/new";
        }
    }

    // 4. 프로젝트별 메인 페이지 진입 (GET /projects/{projectName}/main)
    @GetMapping("/projects/{projectId}/main")
    public String projectMainPage(@PathVariable Long projectId, HttpSession session, Model model) { // ⭐️ HttpSession 추가
        try {
            String userId = getUserId(session);

            if (!projectService.checkUserAccessById(userId, projectId)) {
                throw new SecurityException("접근 권한이 없습니다.");
            }

            String projectName = projectService.getProjectNameById(projectId);

            // ⭐️⭐️⭐️ 닉네임/이름 우선순위 로직 추가 ⭐️⭐️⭐️
            String userName = (String) session.getAttribute("userName");
            String userNickname = (String) session.getAttribute("userNickname");

            String displayName;
            if (userNickname != null && !userNickname.isEmpty()) {
                displayName = userNickname; // 1순위: 닉네임
            } else {
                displayName = userName;     // 2순위: 이름
            }

            // ⭐️ Model에 displayName을 담아 View로 전달 ⭐️
            model.addAttribute("displayName", displayName);

            // layout.html의 title 및 현재 프로젝트명 설정
            model.addAttribute("pageTitle", projectName);
            model.addAttribute("currentProjectName", projectName);

            session.setAttribute("currentProjectId", projectId);

            return "project/index";
        } catch (SecurityException e) {
            return "redirect:/projects";
        } catch (IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }
}