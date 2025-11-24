# Stress Test Results: Project Member Scalability

프로젝트 멤버 1,000명 추가 시 성능 테스트 결과입니다.

## Test Environment
- **Test:** `ProjectMemberStressTest.addManyMembersTest`
- **Data:** 1 Project, 1,000 Users
- **Action:** `projectService.inviteMember` (1,000 times)

## Performance Metrics

| Metric | Result | Note |
| :--- | :--- | :--- |
| **Total Invitation Time** | **1.165s** | 1,000 members added |
| **Avg Time per Member** | **1.165ms** | Very fast |
| **Project Query Time** | **246ms** | **Warning:** Slower than expected for single lookup |

## Analysis
1.  **Invitation Speed:**
    - 멤버 추가 자체는 매우 빠릅니다 (`Set<String>` in memory + Transactional batching effect).
    - 현재 구조에서 쓰기(Write) 성능은 문제가 없습니다.

2.  **Query Performance (N+1 Issue):**
    - 프로젝트 조회 시 `246ms`가 소요되었습니다.
    - 멤버가 1,000명일 때, `ProjectService.findProjectsByUserId` 또는 `getProjectMembers` 호출 시 **1,000번의 추가 쿼리**가 발생할 가능성이 높습니다.
    - 멤버 수가 10,000명으로 늘어나면 조회 시간이 수 초(Seconds) 단위로 늘어날 위험이 있습니다.

## Recommendation
- **Short-term:** 현재 1,000명 수준에서는 사용 가능합니다.
- **Long-term:** 조회 성능 향상을 위해 `User`와 `Project`를 연결하는 별도의 매핑 테이블(`ProjectMember`) 도입과 `JOIN FETCH` 사용을 권장합니다.
