package com.example.capstone25_2.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsById(String id); // id 중복 체크
    boolean existsByEmail(String email); // email 중복 체크
    Optional<User> findById(String id); // 로그인시 아이디 조회
    Optional<User> findByNameAndEmail(String name, String email); // 아이디 찾기 정보
    Optional<User> findByIdAndNameAndEmail(String id, String name, String email); // 비밀번호 찾기 정보
    List<User> findAllByIdIn(Collection<String> ids);
    Optional<User> findByEmail(String email);
}
