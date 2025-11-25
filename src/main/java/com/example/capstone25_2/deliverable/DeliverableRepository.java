package com.example.capstone25_2.deliverable;

import com.example.capstone25_2.deliverable.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {
    List<Deliverable> findByNameContainingOrDescriptionContaining(String name, String description);
}