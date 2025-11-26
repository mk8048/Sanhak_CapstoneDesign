# 📘 GitHub 커밋 이력 조회 API 설계서

---

## 1. API 개요

### **[API-001] GitHub 커밋 리스트 조회**

- **API ID:** API-GITHUB-001
- **API 명:** GitHub 커밋 리스트 조회
- **HTTP Method:** GET
- **URI:** `/github/commits`
- **설명:**
    
    지정된 GitHub 저장소의 브랜치별 커밋 이력을 페이징하여 조회한다.
    
- **담당자:** 차지만

---

## 2. 요청 파라미터 (Request Parameters)

| 파라미터명 | 타입 | 필수 여부 | 설명 | 예시 값 |
| --- | --- | --- | --- | --- |
| owner | String | Y | GitHub 사용자 또는 조직 ID | `mk8048` |
| repo | String | Y | 레포지토리 이름 | `Sanhak_CapstoneDesign` |
| branch | String | N | 조회할 브랜치명 (기본값: `main`) | `dev` |
| page | Integer | N | 현재 페이지 번호 (기본값: 1) | `1` |
| perPage | Integer | N | 페이지당 표시할 커밋 수 (기본값: 20) | `20` |

---

## 3. 응답 데이터 (Response Model / View)

> JSON API가 아니라 Thymeleaf 기반이므로 View로 전달되는 Model 데이터를 명시한다.
> 

| 데이터 명 (Attribute) | 타입 | 설명 |
| --- | --- | --- |
| commits | List | 커밋 정보 리스트 (메시지, 작성자, 날짜, SHA 등) |
| page | Integer | 현재 페이지 번호 (페이지네이션 UI용) |
| owner | String | 입력받은 owner (검색 값 유지) |
| repo | String | 입력받은 repo (검색 값 유지) |

---

## 4. DTO 구조 명세 (상세 설계)

### **[Class] GithubCommitDTO**

| 필드명 | 타입 | 설명 | 비고 |
| --- | --- | --- | --- |
| message | String | 커밋 메시지 |  |
| authorName | String | 커밋 작성자 이름 |  |
| date | String | 커밋 날짜 (ISO 8601) |  |
| html_url | String | 커밋 상세 페이지 URL | 클릭 시 GitHub 페이지 이동 |
| sha | String | 커밋 해시값 | 앞 7자리만 표기 |

---

## 5. 시퀀스 다이어그램 (흐름도)

### **GitHub 외부 API 연동 흐름**

1. **Client(User)** 가 브라우저에서 조회 버튼 클릭
2. **Spring Controller** 가 요청 수신 (`/github/commits`)
3. **GithubService** → GitHub API(`api.github.com`) 호출 (RestTemplate/WebClient)
4. **GitHub API** 가 JSON 데이터 반환
5. Service가 JSON 데이터를 `GithubCommitDTO` 로 변환
6. Controller가 Model에 데이터를 담아 Thymeleaf View로 전달
7. Client에게 렌더링된 HTML 페이지 반환

---

## 6. 예외 처리 (Exception Handling)

| 상황 | HTTP Status | 처리 내용 | 비고 |
| --- | --- | --- | --- |
| 정상 조회 | 200 OK | 커밋 리스트 정상 출력 |  |
| 레포지토리 없음 | 404 Not Found | "존재하지 않는 저장소입니다" 알림 또는 에러 페이지 | GithubApiException 처리 |
| API 호출 한도 초과 | 403 Forbidden | "API 호출 횟수를 초과했습니다" 안내 | GitHub API Rate Limit |
| 네트워크 오류 | 500 Server Error | "GitHub 서버와 통신할 수 없습니다" | 서버/네트워크 장애 |
