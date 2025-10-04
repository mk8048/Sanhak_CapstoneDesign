✅ 테이블 구조 요약
구분	테이블명	주요 내용	관계(FK 기준)
회원 관리	users	회원가입, 로그인, 프로필	-
프로젝트	projects	프로젝트 생성, 방법론 선택	→ users.id
멤버	project_members	프로젝트 멤버 및 권한	→ users.id, projects.prj_id
노트	notes	일반 메모, 회의 내용	→ projects, users
산출물	deliverables	문서, 발표자료, 코드 등	→ projects
회의록	meetings	회의 내용, 녹음 요약	→ projects
일정관리	gantt_tasks	간트차트 일정	→ projects, users
이슈관리	issues	개발 이슈, 해결 상태	→ projects, users
메모대시보드	memos	포스트잇 형태 메모	→ projects, users
기여도	contributions	커밋 수, 코드 변경량	→ projects, users
알림	notifications	이슈/기한/권한 알림	→ projects, users
작업모드	work_sessions	집중 모드, 작업시간	→ projects, users
채팅/소통	messages	팀 채팅, 멘션 기능	→ projects, users
캘린더	calendar_events	일정 등록, 공유	→ projects, users
