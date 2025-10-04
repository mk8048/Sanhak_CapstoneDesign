# 📘 프로젝트 관리 플랫폼 데이터베이스 구조

## 📌 개요
이 데이터베이스는 **캡스톤디자인 프로젝트 관리 플랫폼**의 기능 전반을 지원하기 위해 설계되었습니다.  
기능에는 회원 관리, 프로젝트 생성 및 초대, 산출물 관리, 회의록, 일정(간트 차트),  
이슈 및 메모 관리, 기여도 대시보드, 알림, 집중 작업 모드, 팀 채팅 및 캘린더 기능이 포함됩니다.

---

## 🧱 테이블 구조 요약

| 구분 | 테이블명 | 주요 내용 | 관계 (FK 기준) |
|------|-----------|------------|----------------|
| **회원 관리** | `users` | 회원가입, 로그인, 프로필 관리 | - |
| **프로젝트 관리** | `projects` | 프로젝트 생성, 설명, 방법론 선택 (WATERFALL/AGILE) | → `users.id` |
| **프로젝트 멤버** | `project_members` | 프로젝트 참가 멤버, 역할(PM/PL/MEMBER), 권한(READ/WRITE/ADMIN) | → `users.id`, `projects.prj_id` |
| **노트 / 회의 메모** | `notes` | 프로젝트별 메모, 회의 중 작성 내용 | → `projects.prj_id`, `users.id` |
| **산출물 관리** | `deliverables` | 프로젝트 결과물 (보고서, 코드, 문서, 발표자료 등) | → `projects.prj_id` |
| **회의록** | `meetings` | 회의 내용, 요약문, 녹음 파일 링크 | → `projects.prj_id` |
| **일정 / 간트차트** | `gantt_tasks` | 프로젝트 일정, 시작/종료일, 진행률(%) | → `projects.prj_id`, `users.id` |
| **이슈 관리** | `issues` | 프로젝트 이슈 등록, 상태(OPEN/IN_PROGRESS/RESOLVED) | → `projects.prj_id`, `users.id` |
| **메모 대시보드** | `memos` | 포스트잇 형태의 개인/팀 메모 관리 | → `projects.prj_id`, `users.id` |
| **기여도 대시보드** | `contributions` | 팀원별 커밋 수, 코드 변경량, 기여도 분석 | → `projects.prj_id`, `users.id` |
| **알림 시스템** | `notifications` | 이슈, 마감, 권한 관련 알림 전송 및 확인 | → `projects.prj_id`, `users.id` |
| **작업 모드(집중 모드)** | `work_sessions` | 스타트/정지 버튼 기반 작업 시간 기록 | → `projects.prj_id`, `users.id` |
| **팀 채팅 / 소통방** | `messages` | 팀 내 메시지 교환, 멘션(@user) 기능 | → `projects.prj_id`, `users.id` |
| **캘린더 일정 공유** | `calendar_events` | 일정 등록 및 공유 (회의, 개인 일정 등) | → `projects.prj_id`, `users.id` |

---

## 🔗 주요 관계 다이어그램 개요 (논리 구조)

```text
users (1) ────< project_members >──── (N) projects
   │                                  │
   │                                  ├──< deliverables
   │                                  ├──< meetings
   │                                  ├──< issues
   │                                  ├──< memos
   │                                  ├──< contributions
   │                                  ├──< notifications
   │                                  ├──< work_sessions
   │                                  ├──< messages
   │                                  └──< calendar_events
   │
   ├──< notes
   └──< gantt_tasks
