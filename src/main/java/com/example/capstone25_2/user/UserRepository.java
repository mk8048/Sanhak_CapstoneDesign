package com.example.capstone25_2.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsById(String id); // id 중복 체크
    boolean existsByEmail(String email); // email 중복 체크
    Optional<User> findById(String id); // 로그인시 아이디 조회
}
