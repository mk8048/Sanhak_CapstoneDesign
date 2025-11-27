package com.example.capstone25_2.project;

import com.example.capstone25_2.project.ProjectRole;
import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectMemberDto;
import com.example.capstone25_2.project.dto.ProjectResponse;
import com.example.capstone25_2.project.dto.UpdateProjectRequest;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectWebController {

    private final ProjectService projectService;
    private final UserService userService;

    // 공통 모델 속성 추가 (현재 프로젝트 ID, 사용자 표시 이름)
    @ModelAttribute
    public void addCommonAttributesToModel(HttpSession session, Model model) {
        Long projectId = (Long) session.getAttribute("currentProjectId");
        if (projectId != null) {
            model.addAttribute("currentProjectId", projectId);
        }

        String userName = (String) session.getAttribute("userName");
        String userNickname = (String) session.getAttribute("userNickname");

        if (userName != null) {
            String displayName = (userNickname != null && !userNickname.isEmpty()) ? userNickname : userName;
            model.addAttribute("displayName", displayName);
        }
    }

    // 로그인 상태 확인 및 사용자 ID 반환
    private String getUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        return userId;
    }

    // 프로젝트 목록 페이지 조회
    @GetMapping("/projects")
    public String getProjectList(HttpSession session, Model model) {
        try {
            String userId = getUserId(session);
            model.addAttribute("currentUserId", userId);

            List<ProjectResponse> projects = projectService.findProjectsByUserId(userId);
            model.addAttribute("projectList", projects);

            return "project/project_list";
        } catch (SecurityException e) {
            return "redirect:/user/login";
        }
    }

    // 새 프로젝트 생성 폼 페이지 조회
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

    // 새 프로젝트 생성 처리
    @PostMapping("/project/create")
    public String createProject(@ModelAttribute AddProjectRequest request,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            String creatorId = getUserId(session);
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

    // 프로젝트 메인 페이지 진입
    @GetMapping("/projects/{projectId}/main")
    public String projectMainPage(@PathVariable Long projectId, HttpSession session, Model model) {
        try {
            String userId = getUserId(session);

            if (!projectService.checkUserAccessById(userId, projectId)) {
                throw new SecurityException("접근 권한이 없습니다.");
            }

            String projectName = projectService.getProjectNameById(projectId);
            model.addAttribute("pageTitle", projectName);
            model.addAttribute("currentProjectName", projectName);

            session.setAttribute("currentProjectId", projectId);
            model.addAttribute("currentProjectId", projectId);

            return "project/index";
        } catch (SecurityException | IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }

    // 프로젝트 탈퇴 처리
    @PostMapping("/project/leave")
    public String leaveProject(@RequestParam Long projectId, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login";
        }

        try {
            projectService.leaveProject(userId, projectId);
            redirectAttributes.addFlashAttribute("mypageSuccess", "프로젝트에서 성공적으로 탈퇴했습니다. 다른 프로젝트를 선택해주세요.");
            return "redirect:/projects";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mypageError", e.getMessage());
            return "redirect:/user/mypage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mypageError", "처리 중 알 수 없는 오류가 발생했습니다.");
            return "redirect:/user/mypage";
        }
    }

    // 프로젝트 멤버 관리 페이지 조회
    @GetMapping("/projects/{projectId}/members")
    public String projectMembersPage(@PathVariable Long projectId, HttpSession session, Model model) {
        try {
            String userId = getUserId(session);
            if (!projectService.checkUserAccessById(userId, projectId)) {
                throw new SecurityException("접근 권한이 없습니다.");
            }
            List<ProjectMemberDto> members = projectService.getProjectMembers(projectId);
            Project project = projectService.getProjectById(projectId);
            Long loginUserPk = projectService.getUserPkId(userId);

            model.addAttribute("loginUserPk", loginUserPk);
            model.addAttribute("members", members);
            model.addAttribute("ownerPkId", project.getUsersId());
            model.addAttribute("pageTitle", project.getProjectName());
            model.addAttribute("roles", ProjectRole.values());

            return "project/members";
        } catch (SecurityException | IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }

    // 멤버 역할 변경 처리
    @PostMapping("/project/member/update-role")
    public String updateMemberRole(@RequestParam Long projectId,
                                   @RequestParam String targetUserId,
                                   @RequestParam ProjectRole newRole,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            String userId = getUserId(session);
            projectService.updateMemberRole(projectId, userId, targetUserId, newRole);
            redirectAttributes.addFlashAttribute("inviteSuccess", "멤버 역할이 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }
        return "redirect:/projects/" + projectId + "/members";
    }

    // 팀원 검색 및 조회
    @PostMapping("/projects/search-member")
    public String searchMember(@RequestParam Long projectId,
                               @RequestParam String emailOrId,
                               RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/projects/" + projectId + "/members";

        try {
            User userToInvite = userService.findUserByEmailOrId(emailOrId);

            if (projectService.isUserInProject(userToInvite.getId(), projectId)) {
                throw new IllegalArgumentException("이미 참여 중인 멤버입니다.");
            }
            redirectAttributes.addFlashAttribute("searchedUser", userToInvite);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }
        return redirectUrl;
    }

    // 팀원 초대 처리
    @PostMapping("/projects/invite")
    public String inviteMember(@RequestParam Long projectId,
                               @RequestParam String userIdToInvite,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String inviterId = getUserId(session);

        try {
            projectService.inviteMember(projectId, inviterId, userIdToInvite);
            redirectAttributes.addFlashAttribute("inviteSuccess", "'" + userIdToInvite + "' 님을 성공적으로 초대했습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }
        return "redirect:/projects/" + projectId + "/members";
    }

    // 프로젝트 설정 페이지 조회
    @GetMapping("/projects/{projectId}/settings")
    public String projectSettingsPage(@PathVariable Long projectId, HttpSession session, Model model) {
        try {
            String userId = getUserId(session);

            if (!projectService.checkUserAccessById(userId, projectId)) {
                return "redirect:/projects";
            }

            Project project = projectService.getProjectById(projectId);
            Long loginUserPk = projectService.getUserPkId(userId);

            model.addAttribute("project", project);
            model.addAttribute("isOwner", project.getUsersId().equals(loginUserPk));
            model.addAttribute("pageTitle", project.getProjectName() + " 설정");

            return "settings/settings";
        } catch (Exception e) {
            return "redirect:/projects";
        }
    }

    // 프로젝트 삭제 처리
    @PostMapping("/projects/{projectId}/delete")
    public String deleteProject(@PathVariable Long projectId,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            String userId = getUserId(session);
            projectService.deleteProject(projectId, userId, password);

            redirectAttributes.addFlashAttribute("successMessage", "프로젝트가 성공적으로 삭제되었습니다.");
            return "redirect:/projects";
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 실패: " + e.getMessage());
            return "redirect:/projects/" + projectId + "/settings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "알 수 없는 오류가 발생했습니다.");
            return "redirect:/projects/" + projectId + "/settings";
        }
    }

    // 프로젝트 정보 수정 처리
    @PostMapping("/projects/{projectId}/update")
    public String updateProject(@PathVariable Long projectId,
                                @ModelAttribute UpdateProjectRequest request,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            String userId = getUserId(session);
            projectService.updateProject(projectId, userId, request);
            redirectAttributes.addFlashAttribute("successMessage", "프로젝트 설정이 저장되었습니다.");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "권한 오류: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "수정 실패: " + e.getMessage());
        }
        return "redirect:/projects/" + projectId + "/settings";
    }
}