package com.quizapp.repository;

import com.quizapp.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByNameIgnoreCase(String name);
}
