package com.example.capstone25_2.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 ID로 중복 체크 또는 조회를 위한 메서드 (JPA 쿼리 메서드)
    boolean existsById(String id);

    // 이메일 중복 체크 (DB 스키마에서 email도 unique로 설정했으므로)
    boolean existsByEmail(String email);

    // (참고) 나중에 로그인 시 ID로 사용자를 찾을 때 사용
    // Optional<User> findById(String id);
}
