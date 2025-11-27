package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.ProjectMemberDto;
import com.example.capstone25_2.project.dto.ProjectResponse;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserService;
import jakarta.servlet.http.HttpSession; // HttpSession import
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

            model.addAttribute("currentUserId", userId);

            // Service에서 사용자별 프로젝트 목록 조회
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

            // Service의 변경된 save 메서드 호출 (creatorId 전달)
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
    public String projectMainPage(@PathVariable Long projectId, HttpSession session, Model model) { // HttpSession 추가
        try {
            String userId = getUserId(session);

            if (!projectService.checkUserAccessById(userId, projectId)) {
                throw new SecurityException("접근 권한이 없습니다.");
            }

            String projectName = projectService.getProjectNameById(projectId);

            // layout.html의 title 및 현재 프로젝트명 설정
            model.addAttribute("pageTitle", projectName);
            model.addAttribute("currentProjectName", projectName);

            session.setAttribute("currentProjectId", projectId);
            model.addAttribute("currentProjectId", projectId);

            return "project/index";
        } catch (SecurityException e) {
            return "redirect:/projects";
        } catch (IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }

    @PostMapping("/project/leave")
    public String leaveProject(@RequestParam Long projectId, HttpSession session,
            RedirectAttributes redirectAttributes) {

        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login"; // 로그인 세션 없으면 로그인 페이지로
        }

        try {
            // Service 호출하여 프로젝트 탈퇴
            projectService.leaveProject(userId, projectId);

            redirectAttributes.addFlashAttribute("mypageSuccess", "프로젝트에서 성공적으로 탈퇴했습니다. 다른 프로젝트를 선택해주세요.");

            // 프로젝트 목록 페이지로 리다이렉트
            return "redirect:/projects";

        } catch (IllegalArgumentException e) {
            // 오류 발생 시 마이페이지로 돌아가기 (에러 토스트 메시지 표시)
            redirectAttributes.addFlashAttribute("mypageError", e.getMessage());
            return "redirect:/user/mypage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mypageError", "처리 중 알 수 없는 오류가 발생했습니다.");
            return "redirect:/user/mypage";
        }
    }

    @GetMapping("/projects/{projectId}/members")
    public String projectMembersPage(@PathVariable Long projectId, HttpSession session, Model model) {
        // (이 메서드는 기존 로직 그대로 유지)
        try {
            String userId = getUserId(session);
            if (!projectService.checkUserAccessById(userId, projectId)) {
                throw new SecurityException("접근 권한이 없습니다.");
            }
            List<ProjectMemberDto> members = projectService.getProjectMembers(projectId);
            Project project = projectService.getProjectById(projectId);

            Long loginUserPk = projectService.getUserPkId(userId);
            model.addAttribute("loginUserPk", loginUserPk);
            model.addAttribute("members", members); // 이제 members는 DTO 리스트입니다.
            model.addAttribute("ownerPkId", project.getUsersId());
            model.addAttribute("pageTitle", project.getProjectName());
            // ⭐️ Enum 값을 뷰에서 쓰기 위해 추가 (선택 상자에 사용)
            model.addAttribute("roles", ProjectRole.values());

            return "project/members";

        } catch (SecurityException | IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }

    @PostMapping("/project/member/update-role")
    public String updateMemberRole(@RequestParam Long projectId,
                                   @RequestParam String targetUserId,
                                   @RequestParam ProjectRole newRole,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            String userId = getUserId(session); // 현재 로그인한 사람 (소유자여야 함)
            projectService.updateMemberRole(projectId, userId, targetUserId, newRole);

            redirectAttributes.addFlashAttribute("inviteSuccess", "멤버 역할이 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }
        return "redirect:/projects/" + projectId + "/members";
    }

    // [수정] 1. 팀원 조회 (Search) - 리다이렉트 제거
    @PostMapping("/projects/search-member")
    public String searchMember(@RequestParam Long projectId,
            @RequestParam String emailOrId,
            RedirectAttributes redirectAttributes) { // Model 대신 RedirectAttributes

        // 1. 리다이렉트할 URL을 미리 정의
        String redirectUrl = "redirect:/projects/" + projectId + "/members";

        try {
            // 2. 사용자 조회
            User userToInvite = userService.findUserByEmailOrId(emailOrId);

            // 3. 이미 멤버인지 확인
            if (projectService.isUserInProject(userToInvite.getId(), projectId)) {
                throw new IllegalArgumentException("이미 참여 중인 멤버입니다.");
            }

            // 4. [성공] 찾은 사용자 정보를 Flash Attribute에 담아 리다이렉트
            redirectAttributes.addFlashAttribute("searchedUser", userToInvite);

        } catch (IllegalArgumentException e) {
            // 5. [실패] 에러 메시지를 Flash Attribute에 담아 리다이렉트
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }

        // 6. 원래의 GET 페이지로 리다이렉트
        return redirectUrl;
    }

    // [수정] 2. 팀원 초대 (Invite) - 리다이렉트 유지
    // 초대(데이터 변경)는 리다이렉트(PRG) 패턴을 유지하는 것이 좋습니다.
    @PostMapping("/projects/invite")
    public String inviteMember(@RequestParam Long projectId,
            @RequestParam String userIdToInvite, // HTML의 name="userIdToInvite"와 철자가 정확히 같아야 함
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String inviterId = getUserId(session); // 요청한 사람 (로그인한 사람)

        try {
            // Service 호출 (초대하는 사람 ID, 초대받을 사람 ID 전달)
            projectService.inviteMember(projectId, inviterId, userIdToInvite);

            redirectAttributes.addFlashAttribute("inviteSuccess", "'" + userIdToInvite + "' 님을 성공적으로 초대했습니다.");

        } catch (IllegalArgumentException e) {
            // 사용자 없음, 이미 멤버임 등
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        } catch (SecurityException e) {
            // ⭐️ 권한 없음 (소유자가 아님) -> 이 메시지가 토스트로 떠야 함
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }

        return "redirect:/projects/" + projectId + "/members";
    }
}