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
@RequiredArgsConstructor // ⭐️ 생성자 주입 자동화
public class MemoPageController {

    private final ProjectService projectService; // ⭐️ 역할 조회용 서비스

    // ⭐️ [1] 레이아웃(사이드바/헤더) 공통 데이터 주입
    // (이 코드가 있어야 사이드바에 이름이 뜨고 프로젝트 메뉴가 활성화됩니다)
    @ModelAttribute
    public void addLayoutAttributes(HttpSession session, Model model) {
        // 1. 사용자 이름/닉네임 (displayName)
        String userName = (String) session.getAttribute("userName");
        String userNickname = (String) session.getAttribute("userNickname");

        if (userName != null) {
            String displayName = (userNickname != null && !userNickname.isEmpty()) ? userNickname : userName;
            model.addAttribute("displayName", displayName);
        }

        // 2. 현재 선택된 프로젝트 정보 (currentProjectId, pageTitle)
        Long projectId = (Long) session.getAttribute("currentProjectId");
        if (projectId != null) {
            model.addAttribute("currentProjectId", projectId);

            // 기본적으로 프로젝트 이름을 타이틀로 설정 (개별 메서드에서 덮어쓰기 가능)
            try {
                String projectName = projectService.getProjectNameById(projectId);
                model.addAttribute("pageTitle", projectName);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    // ⭐️ [2] 메모 페이지 조회 (역할 정보 추가)
    @GetMapping("/memo")
    public String memoPage(HttpSession session, Model model) {
        Long projectId = (Long) session.getAttribute("currentProjectId");
        String userId = (String) session.getAttribute("userId");

        // 예외 처리: 프로젝트가 선택되지 않았거나 로그인이 안 된 경우
        if (projectId == null) {
            return "redirect:/projects";
        }
        if (userId == null) {
            return "redirect:/user/login";
        }

        // ⭐️ 핵심: 현재 유저의 역할(Role)을 조회해서 모델에 담음 ⭐️
        // (이 값이 있어야 HTML에서 뷰어일 때 버튼을 숨길 수 있음)
        ProjectRole role = projectService.getUserRoleInProject(projectId, userId);
        model.addAttribute("userRole", role);

        // 페이지 제목 설정 (덮어쓰기)
        model.addAttribute("pageTitle", "메모장");

        return "memo/memo";
    }
}