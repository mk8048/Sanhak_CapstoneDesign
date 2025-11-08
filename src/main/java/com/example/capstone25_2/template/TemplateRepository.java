package com.example.capstone25_2.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long>{

    List<Template> findByCategory(String category);
}
